package com.tttsaurus.ksml

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.language.KsmlFile

@TestDataPath($$"$CONTENT_ROOT/src/test/testData")
class KsmlModuleMetadataTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/testData"

    fun testKsmlFileMetadata1() {
        val ksmlFile = myFixture.addFileToProject(
            "random/mymodule.ksml",
            """
            @module module_a
            @requires module_b
            @gl_version 330 core
            """.trimIndent()
        ) as KsmlFile

        assertEquals("module_a", ksmlFile.moduleName)
        assertEquals("mymodule.ksml", ksmlFile.moduleFileName)
        assertEquals(330, ksmlFile.moduleGlVersion)
        assertEquals("core", ksmlFile.moduleGlVersionIdent)
    }

    fun testKsmlFileMetadata2() {
        val ksmlFile = myFixture.addFileToProject(
            "random/mymodule.ksml",
            """
            @module module_a
            """.trimIndent()
        ) as KsmlFile

        assertEquals("module_a", ksmlFile.moduleName)
        assertEquals("mymodule.ksml", ksmlFile.moduleFileName)
        assertEquals(null, ksmlFile.moduleGlVersion)
        assertEquals(null, ksmlFile.moduleGlVersionIdent)
    }
}
