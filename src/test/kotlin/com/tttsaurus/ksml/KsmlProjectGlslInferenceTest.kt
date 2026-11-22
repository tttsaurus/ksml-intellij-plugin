package com.tttsaurus.ksml

import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.util.xmlb.XmlSerializer
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.utils.ModuleFunctionCallGlVersionChecker
import com.tttsaurus.ksml.settings.KsmlProjectSettings
import com.tttsaurus.ksml.tool_window.KsmlSettingsPanel
import org.jdom.Element
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement
import java.awt.Component
import java.awt.Container
import javax.swing.JButton

class KsmlProjectGlslInferenceTest : BasePlatformTestCase() {

    private val settings get() = project.service<KsmlProjectSettings>()

    override fun tearDown() {
        try {
            settings.loadState(KsmlProjectSettings.SettingsState())
        } finally {
            super.tearDown()
        }
    }

    private fun descendants(container: Container): Sequence<Component> = sequence {
        for (component in container.components) {
            yield(component)
            if (component is Container) yieldAll(descendants(component))
        }
    }

    fun testIsolation() {
        val otherFixture = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("ksml-isolation").fixture
        otherFixture.setUp()

        try {
            val otherProject = otherFixture.project
            assertNotSame(project, otherProject)
            val otherSettings = otherProject.service<KsmlProjectSettings>()
            settings.stdlibSourceUrls = listOf("https://example.com/first")
            assertEmpty(otherSettings.stdlibSourceUrls)
            otherSettings.stdlibSourceUrls = listOf("https://example.com/second")
            assertEquals(listOf("https://example.com/first"), settings.stdlibSourceUrls)
            assertEquals(listOf("https://example.com/second"), otherSettings.stdlibSourceUrls)
        } finally {
            otherFixture.tearDown()
        }
    }

    fun testPersistence() {
        settings.stdlibSourceUrls = listOf("https://example.com/library")
        val restored = XmlSerializer.deserialize(XmlSerializer.serialize(settings.state), KsmlProjectSettings.SettingsState::class.java)
        settings.stdlibSourceUrls = emptyList()
        settings.loadState(restored)
        assertEquals(listOf("https://example.com/library"), settings.stdlibSourceUrls)
        val legacy = Element("state").addContent(Element("option").setAttribute("name", "defaultGlslProfile").setAttribute("value", "COMPATIBLE"))
        settings.loadState(XmlSerializer.deserialize(legacy, KsmlProjectSettings.SettingsState::class.java))
        assertEmpty(settings.stdlibSourceUrls)
        assertEquals("GL330 CORE", GlslVersionRequirement.resolve(330, null)!!.displayName)
    }

    fun testVersions() {
        val module = myFixture.addFileToProject("profile.ksml", """
            @module profile_test
            @gl_version 330 compatibility
            @code ${"\"\"\""}
            int func() { return 1; }
            ${"\"\"\""}
        """.trimIndent())
        val function = PsiTreeUtil.findChildOfType(module, KsmlCodeDecl::class.java)!!
        val shader = myFixture.addFileToProject("profile.glsl", "#version 330\n\nvoid main() {}")

        val result = ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, shader, function)
        assertFalse(result.first)
        assertEquals("GL330 CORE", result.second!!.envGlVersionString)
        assertEquals("GL330 COM", result.second!!.requiredGlVersionString)

        val explicitShader = myFixture.addFileToProject("explicit.glsl", "#version 330 compatibility\n\nvoid main() {}")
        assertTrue(ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, explicitShader, function).first)
    }

    fun testDrafts() {
        val panel = KsmlSettingsPanel(project)
        Disposer.register(testRootDisposable, panel)
        val components = descendants(panel).toList()
        val list = components.filterIsInstance<JBList<*>>().single { it.name == "stdlibSourceUrls" }
        @Suppress("UNCHECKED_CAST")
        val model = list.model as CollectionListModel<String>
        assertFalse(components.any { it.name == "defaultGlslProfile" })
        val buttons = components.filterIsInstance<JButton>()
        val apply = buttons.single { it.text == KsmlBundle.message("settings.apply") }
        val revert = buttons.single { it.text == KsmlBundle.message("settings.revert") }
        val defaults = buttons.single { it.text == KsmlBundle.message("settings.restoreDefaults") }

        assertFalse(apply.isEnabled)
        model.add("https://example.com/library")
        assertEmpty(settings.stdlibSourceUrls)
        assertTrue(apply.isEnabled)
        revert.doClick()
        assertEmpty(model.items)

        model.add("https://example.com/library")
        apply.doClick()
        assertEquals(listOf("https://example.com/library"), settings.stdlibSourceUrls)
        assertFalse(apply.isEnabled)

        defaults.doClick()
        assertEmpty(model.items)
        assertEquals(listOf("https://example.com/library"), settings.stdlibSourceUrls)
        apply.doClick()
        assertEmpty(settings.stdlibSourceUrls)
    }

    fun testFolding() {
        myFixture.configureByText("folding.ksml", """
            @module profile_test
            @gl_version 330
            @code ${"\"\"\""}
            int func() {
                return 1;
            }
            ${"\"\"\""}
        """.trimIndent())
        val manager = CodeFoldingManager.getInstance(project)
        manager.updateFoldRegions(myFixture.editor)
        assertTrue(myFixture.editor.foldingModel.allFoldRegions.single().placeholderText.startsWith("GL330 CORE |"))

        WriteCommandAction.runWriteCommandAction(project) {
            val document = myFixture.editor.document
            val offset = document.text.indexOf("330")
            document.replaceString(offset, offset + 3, "140")
        }
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        manager.updateFoldRegions(myFixture.editor)
        assertTrue(myFixture.editor.foldingModel.allFoldRegions.single().placeholderText.startsWith("GL140 |"))
    }
}
