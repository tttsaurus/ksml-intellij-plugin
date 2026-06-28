package com.tttsaurus.ksml

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.stub.KsmlFunctionNameExtractor

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class KsmlFunctionStubTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun ksmlCodeBlock(glsl: String): String =
        "@code \"\"\"$glsl\n\"\"\"\n"

    private fun findFunction(name: String): Collection<KsmlCodeDecl> {
        return StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            name,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )
    }

    fun testExtractorFindsFunctionDefinition() {
        val name = KsmlFunctionNameExtractor.extractFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int func() {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals("func", name)
    }

    fun testExtractorIgnoresFunctionCallInBody() {
        val name = KsmlFunctionNameExtractor.extractFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int realFunc() {
                    return func();
                }
                """.trimIndent()
            )
        )

        assertEquals("realFunc", name)
    }

    fun testExtractorIgnoresCommentedFunction() {
        val name = KsmlFunctionNameExtractor.extractFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                // int fake() { return 1; }

                int realFunc() {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals("realFunc", name)
    }

    fun testSingleFunctionIsIndexed() {
        myFixture.addFileToProject(
            "stub/function.ksml",
            ksmlCodeBlock(
                """
                int func() {
                    return 1;
                }
                """.trimIndent()
            )
        )

        val result = findFunction("func")

        assertEquals(1, result.size)
        assertTrue(result.single().text.contains("int func"))
    }

    fun testMissingFunctionIsNotIndexed() {
        myFixture.addFileToProject(
            "stub/no_function.ksml",
            ksmlCodeBlock(
                """
                int a = 1;
                int b = 2;
                """.trimIndent()
            )
        )

        assertEmpty(findFunction("func"))
    }

    fun testMultipleCodeBlocksAreIndexed() {
        myFixture.addFileToProject(
            "stub/multi.ksml",
            """
            @code ${"\"\"\""}
            int first() {
                return 1;
            }
            ${"\"\"\""}

            @code ${"\"\"\""}
            float second() {
                return 2.0;
            }
            ${"\"\"\""}
            """.trimIndent()
        )

        assertEquals(1, findFunction("first").size)
        assertEquals(1, findFunction("second").size)
    }

    fun testDuplicateFunctionNamesReturnMultipleDecls() {
        myFixture.addFileToProject(
            "stub/a.ksml",
            ksmlCodeBlock("int func() { return 1; }")
        )

        myFixture.addFileToProject(
            "stub/b.ksml",
            ksmlCodeBlock("int func() { return 2; }")
        )

        assertEquals(2, findFunction("func").size)
    }

    fun testCommentedFunctionIsNotIndexed() {
        myFixture.addFileToProject(
            "stub/comment.ksml",
            ksmlCodeBlock(
                """
                // int func() { return 1; }
                int realFunc() { return 2; }
                """.trimIndent()
            )
        )

        assertEmpty(findFunction("func"))
        assertEquals(1, findFunction("realFunc").size)
    }

    fun testFlawedFunctionIsNotIndexed() {
        myFixture.addFileToProject(
            "stub/flawed.ksml",
            ksmlCodeBlock(
                """
                0int func() { return 1; }
                """.trimIndent()
            )
        )

        assertEmpty(findFunction("func"))
    }
}
