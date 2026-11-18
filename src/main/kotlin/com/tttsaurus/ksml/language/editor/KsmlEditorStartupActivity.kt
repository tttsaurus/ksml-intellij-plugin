package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class KsmlEditorStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().invokeLater({
            if (!project.isDisposed) {
                GlslEditorListener().initializeOpenEditors(FileEditorManager.getInstance(project))
            }
        }, ModalityState.nonModal())
    }
}
