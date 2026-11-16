package com.tttsaurus.ksml.tool_window

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer.GlProfile
import com.tttsaurus.ksml.settings.KsmlProjectSettings
import com.tttsaurus.ksml.settings.KsmlProjectSettings.SettingsState
import com.tttsaurus.ksml.settings.KsmlSettingsListener
import java.awt.BorderLayout
import java.net.URI
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListSelectionModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class KsmlSettingsPanel(private val project: Project) : JPanel(BorderLayout()), Disposable {

    private val settings = project.service<KsmlProjectSettings>()
    private var baseline = snapshot()
    private var disposed = false

    private val profileComboBox = ComboBox(GlProfile.entries.toTypedArray()).apply {
        name = "defaultGlslProfile"
        renderer = SimpleListCellRenderer.create("") { profile ->
            KsmlBundle.message(
                when (profile) {
                    GlProfile.CORE -> "settings.profile.core"
                    GlProfile.COMPATIBLE -> "settings.profile.compatibility"
                }
            )
        }
        selectedItem = baseline.defaultGlslProfile
    }
    private val urlModel = CollectionListModel(baseline.stdlibSourceUrls)
    private val urlList = JBList(urlModel).apply {
        name = "stdlibSourceUrls"
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        emptyText.text = KsmlBundle.message("settings.stdlib.empty")
    }
    private val applyButton = JButton(KsmlBundle.message("settings.apply"))
    private val revertButton = JButton(KsmlBundle.message("settings.revert"))

    val preferredFocusComponent: JComponent get() = profileComboBox

    init {
        border = JBUI.Borders.empty(12)
        add(panel {
            row { label(KsmlBundle.message("settings.project", project.name)) }
            group(KsmlBundle.message("settings.glsl")) {
                row(KsmlBundle.message("settings.profile.label")) { cell(profileComboBox) }
                row { comment(KsmlBundle.message("settings.profile.description")) }
            }
            group(KsmlBundle.message("settings.stdlib")) {
                row { comment(KsmlBundle.message("settings.stdlib.description")) }
                row { cell(createUrlEditor()).align(AlignX.FILL) }
            }
            row {
                cell(applyButton)
                cell(revertButton)
                button(KsmlBundle.message("settings.restoreDefaults")) {
                    setDraft(SettingsState())
                }
            }
        }, BorderLayout.NORTH)

        profileComboBox.addActionListener { updateButtons() }
        urlModel.addListDataListener(object : ListDataListener {
            override fun intervalAdded(event: ListDataEvent) = updateButtons()
            override fun intervalRemoved(event: ListDataEvent) = updateButtons()
            override fun contentsChanged(event: ListDataEvent) = updateButtons()
        })
        applyButton.addActionListener {
            settings.updateSettings(profileComboBox.selectedItem as GlProfile, urlModel.items)
            reset()
        }
        revertButton.addActionListener { reset() }
        project.messageBus.connect(this).subscribe(KsmlSettingsListener.TOPIC, KsmlSettingsListener {
            ToolWindowManager.getInstance(project).invokeLater {
                if (!disposed && !project.isDisposed) {
                    val wasModified = draft() != baseline
                    baseline = snapshot()
                    if (!wasModified) setDraft(baseline)
                    updateButtons()
                }
            }
        })
        updateButtons()
    }

    private fun createUrlEditor(): JPanel = ToolbarDecorator.createDecorator(urlList, urlModel)
        .setAddAction {
            promptUrl("")?.let { url ->
                urlModel.add(url)
                urlList.selectedIndex = urlModel.size - 1
            }
        }
        .setEditAction {
            val index = urlList.selectedIndex
            if (index >= 0) {
                promptUrl(urlModel.getElementAt(index))?.let { url -> urlModel.setElementAt(url, index) }
            }
        }
        .setRemoveAction {
            val index = urlList.selectedIndex
            if (index >= 0) {
                urlModel.remove(index)
                urlList.selectedIndex = minOf(index, urlModel.size - 1)
            }
        }
        .setPreferredSize(JBUI.size(320, 140))
        .createPanel()

    private fun promptUrl(initialValue: String): String? = Messages.showInputDialog(
        project,
        KsmlBundle.message("settings.stdlib.url.label"),
        KsmlBundle.message("settings.stdlib.url.title"),
        null,
        initialValue,
        object : InputValidatorEx {
            override fun getErrorText(inputString: String): String? {
                val uri = runCatching { URI(inputString.trim()) }.getOrNull()
                return if (uri != null && uri.scheme?.lowercase() in setOf("http", "https") && !uri.host.isNullOrBlank()) {
                    null
                } else {
                    KsmlBundle.message("settings.stdlib.url.invalid")
                }
            }
        }
    )?.trim()

    private fun snapshot(): SettingsState = settings.state.copy(stdlibSourceUrls = settings.stdlibSourceUrls)

    private fun draft(): SettingsState = SettingsState(
        defaultGlslProfile = profileComboBox.selectedItem as GlProfile,
        stdlibSourceUrls = urlModel.items.toList()
    )

    private fun setDraft(state: SettingsState) {
        profileComboBox.selectedItem = state.defaultGlslProfile
        urlModel.replaceAll(state.stdlibSourceUrls)
        updateButtons()
    }

    private fun reset() {
        baseline = snapshot()
        setDraft(baseline)
    }

    private fun updateButtons() {
        val modified = draft() != snapshot()
        applyButton.isEnabled = modified
        revertButton.isEnabled = modified
    }

    override fun dispose() {
        disposed = true
    }
}
