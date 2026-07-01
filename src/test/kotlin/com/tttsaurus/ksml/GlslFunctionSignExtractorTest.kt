package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.utils.GlslFunctionSignExtractor

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class GlslFunctionSignExtractorTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun ksmlCodeBlock(glsl: String): String =
        "@code \"\"\"$glsl\n\"\"\"\n"

    fun testExtractorFindsFunctionDefinition() {
        val name = GlslFunctionSignExtractor.extractFuncNameFromCodeBlockTokenText(
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
        val name = GlslFunctionSignExtractor.extractFuncNameFromCodeBlockTokenText(
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
        val name = GlslFunctionSignExtractor.extractFuncNameFromCodeBlockTokenText(
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

    fun testExtractorFindsParamTypes() {
        val names = GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int func(int a, float b) {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals(listOf("int", "float"), names)
    }

    fun testExtractorFindsParamTypesWithQualifiers() {
        val names = GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int func(in int a, out float b, const vec2 c, inout mat3 d) {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals(listOf("int", "float", "vec2", "mat3"), names)
    }

    fun testExtractorFindsParamTypesWithInvalidQualifiers() {
        val names = GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int func(in int a, ok float b, out float c) {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals("int", names[0])
        assertEquals("float", names[2])
    }

    fun testExtractorFindsParamTypesWithArrays() {
        val names = GlslFunctionSignExtractor.extractParamTypesFromCodeBlockTokenText(
            ksmlCodeBlock(
                """
                int func(int[] a, int[][] b, int[1] c[1], int[2] d[], int[1][][] e[][]) {
                    return 1;
                }
                """.trimIndent()
            )
        )

        assertEquals(listOf("int[]", "int[][]", "int[1]", "int[2]", "int[1][][]"), names)
    }
}
