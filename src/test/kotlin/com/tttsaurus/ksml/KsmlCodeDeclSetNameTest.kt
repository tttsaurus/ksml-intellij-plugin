package com.tttsaurus.ksml

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class KsmlCodeDeclSetNameTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun ksmlCodeBlock(glsl: String): String =
        "@code \"\"\"$glsl\n\"\"\"\n"

    private fun findFunction(name: String): Collection<KsmlCodeDecl> {
        return SymbolIndexEntrypoint.getMatchingCodeDecls(project, name)
    }

    fun testCodeDeclSetName() {
        myFixture.addFileToProject(
            "test.ksml",
            ksmlCodeBlock(
                """
                int func() { return 1; }
                """.trimIndent()
            )
        )

        val codeDecls = findFunction("func")

        assertEquals(1, codeDecls.size)

        val codeDecl = codeDecls.first()
        assertEquals("func", codeDecl.name)

        WriteCommandAction.writeCommandAction(project).run<Throwable> {
            codeDecl.setName("newFunc")
        }

        assertEquals("newFunc", codeDecl.name)
    }
}
