package com.tttsaurus.ksml.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.util.messages.Topic

@Service(Service.Level.PROJECT)
@State(name = "KsmlProjectSettings", storages = [Storage("ksml.xml")])
class KsmlProjectSettings(private val project: Project) :
    SerializablePersistentStateComponent<KsmlProjectSettings.SettingsState>(SettingsState()), ModificationTracker, Disposable {

    data class SettingsState(
        @JvmField var stdlibSourceUrls: List<String> = emptyList()
    )

    var stdlibSourceUrls: List<String>
        get() = state.stdlibSourceUrls.toList()
        set(value) {
            val urls = value.toList()
            if (urls == state.stdlibSourceUrls) return
            updateState { it.copy(stdlibSourceUrls = urls) }
            notifyChanged()
        }

    override fun loadState(state: SettingsState) {
        val previous = this.state
        super.loadState(state.copy(stdlibSourceUrls = state.stdlibSourceUrls.toList()))
        if (previous != state) notifyChanged()
    }

    private fun notifyChanged() {
        if (!project.isDisposed) {
            project.messageBus.syncPublisher(KsmlSettingsListener.TOPIC).settingsChanged()
        }
    }

    override fun getModificationCount(): Long = stateModificationCount

    override fun dispose() = Unit
}

fun interface KsmlSettingsListener {

    fun settingsChanged()

    companion object {

        @Topic.ProjectLevel
        @JvmField
        val TOPIC = Topic(KsmlSettingsListener::class.java, Topic.BroadcastDirection.NONE)
    }
}
