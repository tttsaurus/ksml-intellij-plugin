package com.tttsaurus.ksml

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class KsmlFunctionStubMetadataTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun findFunction(name: String): Collection<KsmlCodeDecl> {
        return StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            name,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )
    }

    fun testFunctionGlVersion() {
        myFixture.addFileToProject(
            "stub/test.ksml",
            """
            @module test_module_2

            @gl_version 330

            // asd
            // asdsdsd

            @feature PBR
            @gl_requires 110
            @code ""${'"'}
            int func() {
                int a = 1;
                int b = 123;
                return a;
            }
            ""${'"'}
            """.trimIndent()
        )

        val result = findFunction("func")

        assertEquals(1, result.size)
        assertTrue(result.single().text.contains("int func"))

        val codeDecl = result.single()

        assertEquals(110, codeDecl.funcGlVersion)
        assertEquals(null, codeDecl.funcGlVersionIdent)
        assertEquals(false, codeDecl.isExport)
        assertEquals("PBR", codeDecl.featureRequired)
    }
}
