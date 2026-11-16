package com.tttsaurus.ksml.hotfix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.util.Disposer
import com.intellij.util.concurrency.AppExecutorUtil
import com.tttsaurus.ksml.KsmlBundle
import java.net.SocketTimeoutException
import java.nio.file.Path
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal data class DownloadedHotfix(val release: GlslHotfixRelease, val file: Path)

internal data class GlslHotfixState(
    val repositoryAvailable: Boolean? = null,
    val checkingRepository: Boolean = false,
    val releases: List<GlslHotfixRelease> = emptyList(),
    val selectedTag: String? = null,
    val downloaded: DownloadedHotfix? = null,
    val busy: Boolean = false,
    val installing: Boolean = false,
    val restartPending: Boolean = false,
    val status: String = KsmlBundle.message("hotfix.idle"),
    val error: Boolean = false
) {
    val repositoryReady: Boolean get() = repositoryAvailable == true && !checkingRepository
    val selectedRelease: GlslHotfixRelease? get() = releases.firstOrNull { it.tag == selectedTag }
    val selectedDownload: DownloadedHotfix? get() = downloaded?.takeIf { it.release == selectedRelease }
}

@Service(Service.Level.APP)
internal class GlslHotfixService : Disposable {

    private val installer = GlslHotfixInstaller()
    private val manager = GlslHotfixManager(
        GithubGlslHotfixClient(Path.of(PathManager.getSystemPath(), "ksml", "glsl-hotfix")),
        installer::validate,
        installer::install,
        { ApplicationManager.getApplication().restart() },
        { task -> ApplicationManager.getApplication().executeOnPooledThread(task) },
        { task -> AppExecutorUtil.getAppScheduledExecutorService().schedule(task, 60, TimeUnit.SECONDS) },
        { task -> ApplicationManager.getApplication().invokeLater(task, ModalityState.any()) }
    )

    val state: GlslHotfixState get() = manager.state

    fun subscribe(parent: Disposable, listener: (GlslHotfixState) -> Unit) {
        manager.listeners.add(listener)
        Disposer.register(parent, Disposable { manager.listeners.remove(listener) })
        listener(state)
    }

    fun select(tag: String?) = manager.select(tag)
    fun checkRepository() = manager.checkRepository()
    fun discover() = manager.discover()
    fun download() = manager.download()
    fun installAndRestart() = manager.installAndRestart()
    override fun dispose() = manager.dispose()
}

internal class GlslHotfixManager(
    private val client: GlslHotfixClient,
    private val validate: (GlslHotfixRelease, Path) -> Unit,
    private val install: (GlslHotfixRelease, Path) -> Unit,
    private val restart: () -> Unit,
    private val submit: (() -> Unit) -> Future<*>,
    private val scheduleTimeout: (() -> Unit) -> Future<*>,
    private val dispatch: (() -> Unit) -> Unit
) : Disposable {

    var state = GlslHotfixState()
        private set
    val listeners = mutableSetOf<(GlslHotfixState) -> Unit>()
    private var generation = 0
    private var disposed = false
    private var task: Future<*>? = null
    private var timeout: Future<*>? = null
    private var repositoryGeneration = 0
    private var repositoryTask: Future<*>? = null
    private var repositoryTimeout: Future<*>? = null

    fun checkRepository() {
        if (disposed || state.checkingRepository) return
        val current = ++repositoryGeneration
        update(state.copy(checkingRepository = true))
        repositoryTimeout = scheduleTimeout {
            dispatch {
                if (!disposed && current == repositoryGeneration) {
                    repositoryGeneration++
                    repositoryTask?.cancel(true)
                    update(state.copy(repositoryAvailable = false, checkingRepository = false))
                }
            }
        }
        repositoryTask = submit {
            val available = try {
                client.repositoryExists()
            } catch (_: Exception) {
                false
            }
            dispatch {
                if (!disposed && current == repositoryGeneration) {
                    repositoryGeneration++
                    repositoryTimeout?.cancel(false)
                    update(state.copy(repositoryAvailable = available, checkingRepository = false))
                }
            }
        }
    }

    fun select(tag: String?) {
        if (disposed || !state.repositoryReady || state.busy || state.restartPending || tag == state.selectedTag) return
        if (state.releases.none { it.tag == tag }) return
        update(state.copy(selectedTag = tag, error = false, status = KsmlBundle.message("hotfix.selected")))
    }

    fun discover() = runOperation(KsmlBundle.message("hotfix.discovering"), action = client::discover) { releases ->
        val selected = releases.firstOrNull { it.tag == state.selectedTag } ?: releases.firstOrNull()
        update(state.copy(
            releases = releases,
            selectedTag = selected?.tag,
            status = KsmlBundle.message(if (releases.isEmpty()) "hotfix.empty" else "hotfix.discovered", releases.size)))
    }

    fun download() {
        val release = state.selectedRelease?.takeIf { it.asset != null } ?: return
        runOperation(KsmlBundle.message("hotfix.downloading"), action = {
            val file = client.download(release)
            try {
                validate(release, file)
                DownloadedHotfix(release, file)
            } catch (error: Exception) {
                deleteDownload(file)
                throw error
            }
        }, discard = { deleteDownload(it.file) }) { downloaded ->
            state.downloaded?.let { deleteDownload(it.file) }
            update(state.copy(downloaded = downloaded, status = KsmlBundle.message("hotfix.ready", release.tag)))
        }
    }

    fun installAndRestart() {
        if (disposed || !state.repositoryReady || state.busy || state.restartPending) return
        val downloaded = state.selectedDownload ?: return
        runOperation(KsmlBundle.message("hotfix.installing"), installing = true, action = {
            install(downloaded.release, downloaded.file)
        }) {
            deleteDownload(downloaded.file)
            update(state.copy(downloaded = null, restartPending = true, status = KsmlBundle.message("hotfix.restartPending")))
            restartIde()
        }
    }

    private fun restartIde() {
        try {
            restart()
        } catch (error: Exception) {
            update(state.copy(error = true, status = KsmlBundle.message("hotfix.failed", error.message ?: error.javaClass.simpleName)))
        }
    }

    private fun <T> runOperation(
        status: String,
        installing: Boolean = false,
        action: () -> T,
        discard: (T) -> Unit = {},
        success: (T) -> Unit
    ) {
        if (disposed || !state.repositoryReady || state.busy || state.restartPending) return
        val current = ++generation
        update(state.copy(busy = true, installing = installing, status = status, error = false))
        if (!installing) {
            timeout = scheduleTimeout {
                dispatch {
                    if (!disposed && current == generation) {
                        generation++
                        task?.cancel(true)
                        update(state.copy(busy = false, error = true, status = KsmlBundle.message("hotfix.timeout")))
                    }
                }
            }
        }
        task = submit {
            try {
                val result = action()
                dispatch {
                    if (disposed || current != generation) {
                        discard(result)
                    } else {
                        generation++
                        timeout?.cancel(false)
                        update(state.copy(busy = false, installing = false))
                        success(result)
                    }
                }
            } catch (error: Exception) {
                dispatch {
                    if (!disposed && current == generation) {
                        generation++
                        timeout?.cancel(false)
                        val message = if (error is SocketTimeoutException) KsmlBundle.message("hotfix.timeout")
                            else KsmlBundle.message("hotfix.failed", error.message ?: error.javaClass.simpleName)
                        update(state.copy(busy = false, installing = false, error = true, status = message))
                    }
                }
            }
        }
    }

    private fun update(value: GlslHotfixState) {
        state = value
        listeners.toList().forEach { it(value) }
    }

    override fun dispose() {
        disposed = true
        generation++
        timeout?.cancel(false)
        repositoryTimeout?.cancel(false)
        repositoryTask?.cancel(true)
        if (!state.installing) task?.cancel(true)
        if (!state.restartPending && !state.installing) state.downloaded?.let { deleteDownload(it.file) }
        listeners.clear()
    }
}
