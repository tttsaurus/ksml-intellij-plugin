package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.metadata.KsmlCodeDeclMetadataParser

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class KsmlCodeDeclMetadataTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    private fun ksmlCodeBlock(glsl: String): String =
        "@code \"\"\"$glsl\n\"\"\"\n"

    fun testCodeDeclMetadata1() {
        val codeBlock = ksmlCodeBlock("""
            int func() {
                return 0;
            }
        """.trimIndent())

        val ksmlFile = myFixture.addFileToProject(
            "test.ksml",
            """
            @module module_a
            @requires module_b
            @gl_version 330 core
            
            @export
            @gl_requires 460 core
            @feature OK
            $codeBlock
            """.trimIndent()
        ) as KsmlFile

        val fileContent = ksmlFile.text
        val startOffset = fileContent.indexOf(codeBlock)

        val metadata = KsmlCodeDeclMetadataParser.parse(startOffset, fileContent)

        assertEquals(460, metadata.funcGlVersion)
        assertEquals("core", metadata.funcGlVersionIdent)
        assertEquals(true, metadata.isExport)
        assertEquals("OK", metadata.featureRequired)
    }

    fun testCodeDeclMetadata2() {
        val codeBlock = ksmlCodeBlock("""
            int func() {
                return 0;
            }
        """.trimIndent())

        val ksmlFile = myFixture.addFileToProject(
            "test.ksml",
            """
            @module module_a
            @requires module_b
            @gl_version 330 core
            
            $codeBlock
            """.trimIndent()
        ) as KsmlFile

        val fileContent = ksmlFile.text
        val startOffset = fileContent.indexOf(codeBlock)

        val metadata = KsmlCodeDeclMetadataParser.parse(startOffset, fileContent)

        assertEquals(null, metadata.funcGlVersion)
        assertEquals(null, metadata.funcGlVersionIdent)
        assertEquals(false, metadata.isExport)
        assertEquals(null, metadata.featureRequired)
    }
}
