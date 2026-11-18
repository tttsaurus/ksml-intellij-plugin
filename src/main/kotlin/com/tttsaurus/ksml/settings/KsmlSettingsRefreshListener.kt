package com.tttsaurus.ksml.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.tttsaurus.ksml.language.editor.KsmlEditorRefreshService

class KsmlSettingsRefreshListener(private val project: Project) : KsmlSettingsListener {

    override fun settingsChanged() {
        project.service<KsmlEditorRefreshService>().requestRefresh()
    }
}
