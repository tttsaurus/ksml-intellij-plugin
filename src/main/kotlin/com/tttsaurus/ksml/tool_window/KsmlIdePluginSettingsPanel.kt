package com.tttsaurus.ksml.tool_window

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.hotfix.GlslHotfixRelease
import com.tttsaurus.ksml.hotfix.GlslHotfixService
import com.tttsaurus.ksml.hotfix.GlslHotfixState
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.event.HierarchyEvent
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JProgressBar

class KsmlIdePluginSettingsPanel : JPanel(BorderLayout()), Disposable {

    private val hotfix = service<GlslHotfixService>()
    private var updating = false
    private val repositoryStatus = JBLabel().apply { name = "hotfixRepositoryStatus" }
    private val page: JPanel
    private val releases = object : ComboBox<GlslHotfixRelease>(JBUI.scale(220)) {
        override fun getMinimumPopupWidth(): Int = width.takeIf { it > 0 } ?: preferredSize.width
    }.apply {
        name = "glslHotfixRelease"
        renderer = SimpleListCellRenderer.create("") { release -> release.toString() }
    }
    private val discover = JButton(KsmlBundle.message("hotfix.discover")).apply { name = "discoverHotfix" }
    private val download = JButton(KsmlBundle.message("hotfix.download")).apply { name = "downloadHotfix" }
    private val install = JButton(KsmlBundle.message("hotfix.install")).apply { name = "installHotfix" }
    private val repository = JButton(KsmlBundle.message("hotfix.repository"))
    private val progress = JProgressBar().apply { isIndeterminate = true; name = "hotfixProgress" }
    private val status = JBTextArea().apply {
        name = "hotfixStatus"
        columns = 20
        lineWrap = true
        wrapStyleWord = true
        isEditable = false
        isOpaque = false
        font = JBUI.Fonts.label()
    }
    private val statusArea = JPanel(BorderLayout(0, JBUI.scale(8))).apply {
        name = "hotfixStatusArea"
        border = JBUI.Borders.compound(JBUI.Borders.customLine(JBColor.border()), JBUI.Borders.empty(12))
        add(progress, BorderLayout.NORTH)
        add(status, BorderLayout.CENTER)
    }
    private val statusSection = JPanel(BorderLayout()).apply {
        border = JBUI.Borders.emptyLeft(20)
        add(statusArea, BorderLayout.CENTER)
    }
    private val asset = JBLabel()

    val preferredFocusComponent: JComponent get() = this

    init {
        isFocusable = true
        border = JBUI.Borders.empty(12)
        val version = PluginManagerCore.getPlugin(PluginId.getId("OpenGL-Plugin"))?.version ?: "?"
        val controls = panel {
            group(KsmlBundle.message("hotfix.title")) {
                row { comment(KsmlBundle.message("hotfix.description")) }
                row { label(KsmlBundle.message("hotfix.installed", version)) }
                row { cell(repository) }
                row { cell(discover) }
                row(KsmlBundle.message("hotfix.release")) { cell(releases) }
                row { cell(asset) }
                row { cell(download) }
                row { cell(install) }
            }
        }
        page = JPanel(BorderLayout(0, JBUI.scale(40))).apply {
            add(controls, BorderLayout.NORTH)
            add(statusSection, BorderLayout.CENTER)
        }
        add(panel {
            row { label(KsmlBundle.message("settings.ide.scope")) }
            row(KsmlBundle.message("hotfix.repository.label")) { cell(repositoryStatus) }
            row { cell(page).align(AlignX.FILL) }
        }, BorderLayout.NORTH)
        releases.addActionListener {
            if (!updating) hotfix.select((releases.selectedItem as? GlslHotfixRelease)?.tag)
        }
        discover.addActionListener { hotfix.discover() }
        download.addActionListener { hotfix.download() }
        install.addActionListener { hotfix.installAndRestart() }
        repository.addActionListener { BrowserUtil.browse("https://github.com/tttsaurus/glsl-plugin-idea") }
        hotfix.subscribe(this, ::refresh)
        addHierarchyListener { event ->
            if (event.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong() != 0L && isShowing) {
                hotfix.checkRepository()
            }
        }
    }

    internal fun refresh(state: GlslHotfixState) {
        updating = true
        try {
            val items = (0 until releases.itemCount).map { releases.getItemAt(it) }
            if (items != state.releases) releases.model = DefaultComboBoxModel(state.releases.toTypedArray())
            releases.selectedItem = state.selectedRelease
        } finally {
            updating = false
        }
        repositoryStatus.text = KsmlBundle.message(when {
            state.checkingRepository -> "hotfix.repository.checking"
            state.repositoryAvailable == true -> "hotfix.repository.exists"
            state.repositoryAvailable == false -> "hotfix.repository.missing"
            else -> "hotfix.repository.unknown"
        })
        repositoryStatus.foreground = when {
            state.checkingRepository || state.repositoryAvailable == null -> JBUI.CurrentTheme.Label.foreground()
            state.repositoryAvailable -> JBColor(0x2E7D32, 0x73C991)
            else -> JBColor.RED
        }
        setPageEnabled(page, state.repositoryReady)
        val enabled = state.repositoryReady && !state.busy && !state.restartPending
        releases.isEnabled = enabled && state.releases.isNotEmpty()
        discover.isEnabled = enabled
        download.isEnabled = enabled && state.selectedRelease?.asset != null
        install.isVisible = !state.restartPending
        install.isEnabled = enabled && state.selectedDownload != null
        repository.isEnabled = state.repositoryReady && !state.busy
        progress.isVisible = state.busy || state.checkingRepository
        status.text = state.status
        status.foreground = if (state.error) JBColor.RED else JBUI.CurrentTheme.Label.foreground()
        asset.text = state.selectedRelease?.asset?.name ?: KsmlBundle.message("hotfix.noAsset")
    }

    private fun setPageEnabled(component: Component, enabled: Boolean) {
        component.isEnabled = enabled
        if (component is Container) component.components.forEach { setPageEnabled(it, enabled) }
    }

    override fun dispose() = Unit
}
