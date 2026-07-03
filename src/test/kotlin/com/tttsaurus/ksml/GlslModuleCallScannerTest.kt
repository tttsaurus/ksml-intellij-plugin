package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallScanner

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class GlslModuleCallScannerTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun scan(text: String): List<GlslModuleCallScanner.ModuleCall> {
        val scanner = GlslModuleCallScanner(text)
        val result = mutableListOf<GlslModuleCallScanner.ModuleCall>()
        while (true) {
            val call = scanner.nextCall() ?: break
            result += call
        }
        return result
    }

    fun testSingleCall() {
        val result = scan(
            """
            lighting.pbr()
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting", result[0].module)
        assertEquals("pbr", result[0].function)
    }

    fun testMultipleCalls() {
        val result = scan(
            """
            lighting.pbr();
            lighting1.pbr();
            lighting2.pbr();
            """.trimIndent()
        )

        assertSize(3, result)

        assertEquals("lighting", result[0].module)
        assertEquals("lighting1", result[1].module)
        assertEquals("lighting2", result[2].module)
    }

    fun testSimpleCodeBlock() {
        val result = scan(
            """
            @code ${"\"\"\""}
            int func() {
                lighting.pbr();
                return 0;
            }
            ${"\"\"\""}
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting", result[0].module)
        assertEquals("pbr", result[0].function)
    }

    fun testSimpleCodeBlocks() {
        val result = scan(
            """
            @code ${"\"\"\""}
            int func() {
                lighting.pbr();
                lighting.pbr2();
                return 0;
            }
            ${"\"\"\""}
            
            // comment
            
            @code ${"\"\"\""}
            int func2() {
                yes.no();
                return 0;
            }
            ${"\"\"\""}
            """.trimIndent()
        )

        assertSize(3, result)

        assertEquals("lighting", result[0].module)
        assertEquals("pbr", result[0].function)
        assertEquals("lighting", result[1].module)
        assertEquals("pbr2", result[1].function)
        assertEquals("yes", result[2].module)
        assertEquals("no", result[2].function)
    }

    fun testWhitespace() {
        val result = scan(
            """
            lighting.pbr      (
                 );
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting", result[0].module)
        assertEquals("pbr", result[0].function)
    }

    fun testIdentifier() {
        val result = scan(
            """
            module_123.func_456();
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("module_123", result[0].module)
        assertEquals("func_456", result[0].function)
    }

    fun testLineComment() {
        val result = scan(
            """
            // lighting.pbr();
            lighting_.real();
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting_", result[0].module)
        assertEquals("real", result[0].function)
    }

    fun testBlockComment() {
        val result = scan(
            """
            /*
                lighting.pbr();
            */
            lighting_.real();
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting_", result[0].module)
        assertEquals("real", result[0].function)
    }

    fun testString() {
        val result = scan(
            """
            "
                lighting.pbr();
            "
            "lighting.pbr2()"
            "\"lighting.pbr2()\""
            lighting_.real();
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting_", result[0].module)
        assertEquals("real", result[0].function)
    }

    fun testChar() {
        val result = scan(
            """
            '
                lighting.pbr();
            '
            'lighting.pbr2()'
            '\'lighting.pbr2()\''
            lighting_.real();
            """.trimIndent()
        )

        assertSize(1, result)

        assertEquals("lighting_", result[0].module)
        assertEquals("real", result[0].function)
    }

    fun testPreprocessor() {
        val result = scan(
            """
            #define lighting.pbr();
            #define ok
                lighting.escaped();
            lighting_.real();
            """.trimIndent()
        )

        assertSize(2, result)

        assertEquals("lighting", result[0].module)
        assertEquals("escaped", result[0].function)
        assertEquals("lighting_", result[1].module)
        assertEquals("real", result[1].function)
    }

    fun testNonCall() {
        val result = scan(
            """
            lighting.pbr;
            """.trimIndent()
        )

        assertSize(0, result)
    }

    fun testCalls() {
        val result = scan(
            """
            module.func();module.func();module.func();
            """.trimIndent()
        )

        assertSize(3, result)
    }

    fun testComplicatedNestedCalls() {
        val result = scan(
            """
            module.func(
                module.func(
                    module.func(
                        1 
                    ),
                    2 // ok.fake();
                ) // "\"im.fake();\""
            );
            """.trimIndent()
        )

        assertSize(3, result)
    }

    fun testComplicatedCodeBlocks() {
        val result = scan(
            """
            @code ${"\"\"\""}
            int func() {
                module.func(
                    module.func(
                        module.func(
                            1 
                        ),
                        2 // ok.fake();
                    ) // "\"im.fake();\""
                );
                return 0;
            }
            ${"\"\"\""}
            // comment \"\"\" who.who(); \"\"\"
            @code ${"\"\"\""}
            int func2() {
                yes.no();no.yes();
                return 0;
            }
            ${"\"\"\""}
            """.trimIndent()
        )

        assertSize(5, result)

        assertEquals("module", result[0].module)
        assertEquals("module", result[1].module)
        assertEquals("module", result[2].module)
        assertEquals("yes", result[3].module)
        assertEquals("no", result[4].module)
    }
}
