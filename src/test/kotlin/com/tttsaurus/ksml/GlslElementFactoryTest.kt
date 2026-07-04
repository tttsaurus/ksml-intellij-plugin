package com.tttsaurus.ksml

import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.elementType
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.GlslLanguage

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class GlslElementFactoryTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    fun testPsiFileFactoryCreatesElement1() {
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            GlslLanguage.GLSL_LANGUAGE,
            """
            void testFunc() { }
            """.trimIndent()
        )

        val element = file.findElementAt(5)

        assertNotNull(element)
        element!!

        assertEquals("testFunc", element.text)
        assertEquals("testFunc", element.parent?.text)
        assertEquals("VARIABLE_IDENTIFIER", element.parent?.elementType.toString())
    }

    fun testPsiFileFactoryCreatesElement2() {
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            GlslLanguage.GLSL_LANGUAGE,
            """
            void main() { a.func(); }
            """.trimIndent()
        )

        val element = file.findElementAt(17)

        assertNotNull(element)
        element!!

        assertEquals("func", element.text)
        assertEquals("func", element.parent?.text)
        assertEquals("VARIABLE_IDENTIFIER", element.parent?.elementType.toString())
    }
}
