package com.tttsaurus.ksml.hotfix

import com.intellij.ide.plugins.PluginNode
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.updateSettings.impl.PluginDownloader
import com.intellij.openapi.util.JDOMUtil
import com.tttsaurus.ksml.KsmlBundle
import java.io.IOException
import java.nio.file.Path
import java.util.jar.JarFile

internal class GlslHotfixInstaller {

    fun validate(release: GlslHotfixRelease, file: Path) {
        validateArchive(release, file)
    }

    fun install(release: GlslHotfixRelease, file: Path) {
        validate(release, file)
        val downloader = prepareInstaller(release, file)
        downloader.install()
    }

    companion object {

        internal fun prepareInstaller(release: GlslHotfixRelease, file: Path): PluginDownloader {
            val node = PluginNode(PluginId.getId("OpenGL-Plugin")).apply {
                name = "GLSL"
                version = release.tag.removePrefix("v")
                downloadUrl = file.toUri().toString()
            }
            val errors = mutableListOf<String>()
            val downloader = PluginDownloader.createDownloader(node, "https://github.com/tttsaurus/glsl-plugin-idea", null)
                .withErrorsConsumer { errors.add(it) }
            if (!downloader.prepareToInstall(EmptyProgressIndicator())) {
                throw IOException(errors.joinToString("\n").ifEmpty { KsmlBundle.message("hotfix.error.install") })
            }
            return downloader
        }

        internal fun validateArchive(release: GlslHotfixRelease, file: Path) {
            GithubGlslHotfixClient.verifyDigest(file, release.asset?.digest)
            JarFile(file.toFile()).use { jar ->
                val entry = jar.getJarEntry("META-INF/plugin.xml") ?: throw IOException(KsmlBundle.message("hotfix.error.archive"))
                val descriptor = jar.getInputStream(entry).use { JDOMUtil.load(it) }
                if (descriptor.getChildTextTrim("id") != "OpenGL-Plugin" ||
                    descriptor.getChildTextTrim("version") != release.tag.removePrefix("v")) {
                    throw IOException(KsmlBundle.message("hotfix.error.archive"))
                }
            }
        }
    }
}
