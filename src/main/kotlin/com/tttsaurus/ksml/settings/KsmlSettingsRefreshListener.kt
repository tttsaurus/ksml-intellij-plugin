package com.tttsaurus.ksml.settings

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project

class KsmlSettingsRefreshListener(private val project: Project) : KsmlSettingsListener {

    override fun settingsChanged() {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater

            DaemonCodeAnalyzer.getInstance(project).restart()
            val foldingManager = CodeFoldingManager.getInstance(project)
            for (editor in EditorFactory.getInstance().allEditors) {
                if (editor.project == project && !editor.isDisposed) {
                    foldingManager.scheduleAsyncFoldingUpdate(editor)
                }
            }
        }
    }
}
