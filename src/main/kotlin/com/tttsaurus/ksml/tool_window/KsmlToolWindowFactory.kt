package com.tttsaurus.ksml.tool_window

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.services.MyProjectService
import javax.swing.JButton

class KsmlToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(KsmlBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(KsmlBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = KsmlBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }
}
