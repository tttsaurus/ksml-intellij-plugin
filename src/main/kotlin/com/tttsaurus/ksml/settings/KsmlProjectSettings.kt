package com.tttsaurus.ksml.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.SerializablePersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ModificationTracker
import com.intellij.util.messages.Topic
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer.GlProfile

@Service(Service.Level.PROJECT)
@State(name = "KsmlProjectSettings", storages = [Storage("ksml.xml")])
class KsmlProjectSettings(private val project: Project) :
    SerializablePersistentStateComponent<KsmlProjectSettings.SettingsState>(SettingsState()), ModificationTracker, Disposable {

    data class SettingsState(
        @JvmField var defaultGlslProfile: GlProfile = GlProfile.COMPATIBLE,
        @JvmField var stdlibSourceUrls: List<String> = emptyList()
    )

    var defaultGlslProfile: GlProfile
        get() = state.defaultGlslProfile
        set(value) {
            if (value == state.defaultGlslProfile) return
            updateState { it.copy(defaultGlslProfile = value) }
            notifyChanged()
        }

    var stdlibSourceUrls: List<String>
        get() = state.stdlibSourceUrls.toList()
        set(value) {
            val urls = value.toList()
            if (urls == state.stdlibSourceUrls) return
            updateState { it.copy(stdlibSourceUrls = urls) }
            notifyChanged()
        }

    fun updateSettings(defaultGlslProfile: GlProfile, stdlibSourceUrls: List<String>) {
        val urls = stdlibSourceUrls.toList()
        if (defaultGlslProfile == state.defaultGlslProfile && urls == state.stdlibSourceUrls) return
        updateState { it.copy(defaultGlslProfile = defaultGlslProfile, stdlibSourceUrls = urls) }
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
