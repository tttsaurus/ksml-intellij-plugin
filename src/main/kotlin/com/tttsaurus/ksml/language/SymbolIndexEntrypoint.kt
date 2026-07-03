package com.tttsaurus.ksml.language

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.indexing.FileBasedIndex
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.index.MODULE_INDEX_NAME

object SymbolIndexEntrypoint {

    private const val CATCHING: Boolean = true

    fun getMatchingFiles(project: Project, moduleName: String): Collection<VirtualFile> {
        return if (CATCHING)
            getMatchingFilesCatching(project, moduleName)
        else
            getMatchingFilesNoCatching(project, moduleName)
    }

    private fun getMatchingFilesCatching(project: Project, moduleName: String): Collection<VirtualFile> {
        val files = runCatching {
            FileBasedIndex.getInstance().getContainingFiles(
                MODULE_INDEX_NAME,
                moduleName,
                GlobalSearchScope.projectScope(project)
            )
        }.getOrNull() ?: return emptyList()
        return files
    }

    private fun getMatchingFilesNoCatching(project: Project, moduleName: String): Collection<VirtualFile> {
        return FileBasedIndex.getInstance().getContainingFiles(
            MODULE_INDEX_NAME,
            moduleName,
            GlobalSearchScope.projectScope(project)
        )
    }

    fun getMatchingCodeDecls(project: Project, functionName: String): Collection<KsmlCodeDecl> {
        return if (CATCHING)
            getMatchingCodeDeclsCatching(project, functionName)
        else
            getMatchingCodeDeclsNoCatching(project, functionName)
    }

    private fun getMatchingCodeDeclsCatching(project: Project, functionName: String): Collection<KsmlCodeDecl> {
        val decls = runCatching {
            StubIndex.getElements(
                FUNCTION_INDEX_KEY,
                functionName,
                project,
                GlobalSearchScope.projectScope(project),
                KsmlCodeDecl::class.java
            )
        }.getOrNull() ?: return emptyList()
        return decls
    }

    private fun getMatchingCodeDeclsNoCatching(project: Project, functionName: String): Collection<KsmlCodeDecl> {
        return StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            functionName,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )
    }
}
