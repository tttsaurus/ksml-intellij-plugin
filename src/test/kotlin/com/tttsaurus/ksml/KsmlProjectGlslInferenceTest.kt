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
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer.GlProfile
import com.tttsaurus.ksml.settings.KsmlProjectSettings
import com.tttsaurus.ksml.tool_window.KsmlSettingsPanel
import org.jdom.Element
import java.awt.Component
import java.awt.Container
import javax.swing.JButton
import javax.swing.JComboBox

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

    fun testProfiles() {
        assertEquals(GlProfile.COMPATIBLE, GlslProfileInferencer.getDefaultProfile(project))
        settings.defaultGlslProfile = GlProfile.CORE

        for (identifier in listOf(null, "", "unknown")) {
            assertEquals(GlProfile.CORE, GlslProfileInferencer.getProfile(project, identifier))
            assertEquals("C", GlslProfileInferencer.getProfileDescSymbol(project, identifier))
        }
        assertEquals(GlProfile.CORE, GlslProfileInferencer.getProfile(project, "core"))
        for (identifier in listOf("compat", "compatibility")) {
            assertEquals(GlProfile.COMPATIBLE, GlslProfileInferencer.getProfile(project, identifier))
            assertEquals("", GlslProfileInferencer.getProfileDescSymbol(project, identifier))
        }

        assertEquals(-1, GlslProfileInferencer.compareProfiles(project, 330, null, 330, "compatibility"))
        assertEquals(1, GlslProfileInferencer.compareProfiles(project, 460, null, 330, "compatibility"))
    }

    fun testIsolation() {
        val otherFixture = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("ksml-isolation").fixture
        otherFixture.setUp()

        try {
            val otherProject = otherFixture.project
            assertNotSame(project, otherProject)
            val otherSettings = otherProject.service<KsmlProjectSettings>()
            settings.defaultGlslProfile = GlProfile.CORE
            assertEquals(GlProfile.CORE, GlslProfileInferencer.getDefaultProfile(project))
            assertEquals(GlProfile.COMPATIBLE, GlslProfileInferencer.getDefaultProfile(otherProject))

            otherSettings.defaultGlslProfile = GlProfile.CORE
            settings.defaultGlslProfile = GlProfile.COMPATIBLE
            assertEquals(GlProfile.CORE, GlslProfileInferencer.getDefaultProfile(otherProject))
            assertEquals(GlProfile.COMPATIBLE, GlslProfileInferencer.getDefaultProfile(project))
        } finally {
            otherFixture.tearDown()
        }
    }

    fun testPersistence() {
        settings.defaultGlslProfile = GlProfile.CORE
        val xml = XmlSerializer.serialize(settings.state)
        val restored = XmlSerializer.deserialize(xml, KsmlProjectSettings.SettingsState::class.java)

        settings.defaultGlslProfile = GlProfile.COMPATIBLE
        settings.loadState(restored)
        assertEquals(GlProfile.CORE, GlslProfileInferencer.getDefaultProfile(project))

        settings.loadState(XmlSerializer.deserialize(Element("state"), KsmlProjectSettings.SettingsState::class.java))
        assertEquals(GlProfile.COMPATIBLE, GlslProfileInferencer.getDefaultProfile(project))
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

        assertTrue(ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, shader, function).first)
        settings.defaultGlslProfile = GlProfile.CORE
        val result = ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, shader, function)
        assertFalse(result.first)
        assertEquals("GL330C", result.second!!.envGlVersionString)
        assertEquals("GL330", result.second!!.requiredGlVersionString)

        val explicitShader = myFixture.addFileToProject("explicit.glsl", "#version 330 compatibility\n\nvoid main() {}")
        assertTrue(ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, explicitShader, function).first)
    }

    fun testDrafts() {
        val panel = KsmlSettingsPanel(project)
        Disposer.register(testRootDisposable, panel)
        val components = descendants(panel).toList()
        val combo = components.filterIsInstance<JComboBox<*>>().single { it.name == "defaultGlslProfile" }
        val buttons = components.filterIsInstance<JButton>()
        val apply = buttons.single { it.text == KsmlBundle.message("settings.apply") }
        val revert = buttons.single { it.text == KsmlBundle.message("settings.revert") }
        val defaults = buttons.single { it.text == KsmlBundle.message("settings.restoreDefaults") }

        assertFalse(apply.isEnabled)
        combo.selectedItem = GlProfile.CORE
        assertEquals(GlProfile.COMPATIBLE, settings.defaultGlslProfile)
        assertTrue(apply.isEnabled)
        revert.doClick()
        assertEquals(GlProfile.COMPATIBLE, combo.selectedItem)

        combo.selectedItem = GlProfile.CORE
        apply.doClick()
        assertEquals(GlProfile.CORE, settings.defaultGlslProfile)
        assertFalse(apply.isEnabled)

        defaults.doClick()
        assertEquals(GlProfile.COMPATIBLE, combo.selectedItem)
        assertEquals(GlProfile.CORE, settings.defaultGlslProfile)
        apply.doClick()
        assertEquals(GlProfile.COMPATIBLE, settings.defaultGlslProfile)
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
        assertTrue(myFixture.editor.foldingModel.allFoldRegions.single().placeholderText.startsWith("GL330 |"))

        settings.defaultGlslProfile = GlProfile.CORE
        manager.updateFoldRegions(myFixture.editor)
        assertTrue(myFixture.editor.foldingModel.allFoldRegions.single().placeholderText.startsWith("GL330C |"))
    }
}
