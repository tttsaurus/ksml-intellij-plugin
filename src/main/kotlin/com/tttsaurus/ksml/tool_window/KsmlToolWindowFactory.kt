package com.tttsaurus.ksml.tool_window

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.tttsaurus.ksml.KsmlBundle

class KsmlToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = KsmlSettingsPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, KsmlBundle.message("settings.title"), false)
        content.setDisposer(panel)
        content.preferredFocusableComponent = panel.preferredFocusComponent
        toolWindow.contentManager.addContent(content)

        val idePanel = KsmlIdePluginSettingsPanel()
        val ideContent = ContentFactory.getInstance().createContent(idePanel, KsmlBundle.message("settings.ide.title"), false)
        ideContent.setDisposer(idePanel)
        ideContent.preferredFocusableComponent = idePanel.preferredFocusComponent
        toolWindow.contentManager.addContent(ideContent)
    }
}
