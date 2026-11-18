package com.tttsaurus.ksml

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.stubs.Stub
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubTreeLoader
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.stub.KsmlCodeDeclStub
import com.tttsaurus.ksml.language.stub.KsmlFileStub
import com.tttsaurus.ksml.language.stub.KsmlFileStubElementType

class KsmlStubStorageTest : BasePlatformTestCase() {

    private val source = """
        @module math
        @gl_version 330 core
        @requires functional

        @export
        @code ${"\"\"\""}
        int add_checked(int a, int b) { return a + b; }
        ${"\"\"\""}

        @code ${"\"\"\""}
        int internal(int a) { return functional.id(a); }
        ${"\"\"\""}
    """.trimIndent() + "\n"

    private fun checkStub(root: Stub) {
        assertTrue("Root must be a KSML file stub", root is KsmlFileStub)
        val fileStub = root as KsmlFileStub
        assertSame(KsmlFileStubElementType, fileStub.type)
        val functions = fileStub.childrenStubs.filterIsInstance<KsmlCodeDeclStub>()
        assertEquals(listOf("add_checked", "internal"), functions.map { it.functionName })
        assertEquals(listOf("int", "int"), functions.first().params)
        assertEquals(330, functions.first().moduleGlVersion)
        assertEquals("core", functions.first().moduleGlVersionIdent)
        assertTrue(functions.first().isExport)
        assertFalse(functions.last().isExport)
    }

    fun testRoot() {
        val file = myFixture.addFileToProject("math.ksml", source)
        val root = KsmlFileStubElementType.builder.buildStubTree(file)
        checkStub(root)
    }

    fun testFileType() {
        val file = myFixture.addFileToProject("type.ksml", source)
        val registered = FileTypeManager.getInstance().getFileTypeByExtension("ksml")
        assertSame(registered, file.fileType)
        assertSame(file.fileType, file.fileType)
    }

    fun testStored() {
        val file = myFixture.addFileToProject("stored.ksml", source)
        assertEquals(1, StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            "add_checked",
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        ).size)
        val tree = StubTreeLoader.getInstance().readFromVFile(project, file.virtualFile)
        assertNotNull("Function index entries must have a stored stub tree", tree)
        checkStub(tree!!.root)
    }

    fun testReindex() {
        val file = myFixture.addFileToProject("math.ksml", source)

        fun find(name: String): Collection<KsmlCodeDecl> = StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            name, project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )

        assertEquals(1, find("add_checked").size)
        assertEquals(1, find("internal").size)
        val manager = PsiDocumentManager.getInstance(project)
        val document = manager.getDocument(file)!!
        WriteCommandAction.runWriteCommandAction(project) { document.setText("@module math\n") }
        manager.commitDocument(document)
        assertEmpty(find("add_checked"))
        assertEmpty(find("internal"))
        WriteCommandAction.runWriteCommandAction(project) { document.setText(source) }
        manager.commitDocument(document)
        assertEquals(1, find("add_checked").size)
        assertEquals(1, find("internal").size)
    }
}
