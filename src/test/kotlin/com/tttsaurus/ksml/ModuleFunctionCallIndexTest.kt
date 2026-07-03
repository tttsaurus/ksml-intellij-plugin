package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class ModuleFunctionCallIndexTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    fun testModuleFunctionCallIndex() {
        myFixture.addFileToProject(
            "a.ksml",
            """
            @module module_a
            @requires module_b
            @gl_version 330 core
            
            @code ${"\"\"\""}
            int func() {
                return mymodule.call();
            }
            ${"\"\"\""}
            """.trimIndent()
        )

        myFixture.addFileToProject(
            "b.glsl",
            """
            #version 330 core
            
            void main() {
                mymodule.call();
            }
            """.trimIndent()
        )

        val map = SymbolIndexEntrypoint.getMatchingFunctionCalls(
            project,
            "mymodule",
            "call"
        )

        var counter = 0
        for (entry in map) {
            for (item in entry.value) {
                counter++
            }
        }

        assertEquals(2, counter)
    }
}
