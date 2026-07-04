package com.tttsaurus.ksml.language

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.indexing.FileBasedIndex
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.index.MODULE_INDEX_NAME
import com.tttsaurus.ksml.language.index.modulecall.MODULE_FUNCTION_CALL_INDEX_NAME
import com.tttsaurus.ksml.language.index.modulecall.ModuleFunctionCallKey
import com.tttsaurus.ksml.language.index.modulecall.ModuleFunctionCallLocation

object SymbolIndexEntrypoint {

    private const val CATCHING: Boolean = true

    fun getAllFunctionNames(project: Project): Collection<String> {
        return if (CATCHING)
            getAllFunctionNamesCatching(project)
        else
            getAllFunctionNamesNoCatching(project)
    }

    private fun getAllFunctionNamesCatching(project: Project): Collection<String> {
        return runCatching {
            StubIndex.getInstance().getAllKeys(FUNCTION_INDEX_KEY, project)
        }.getOrNull() ?: emptyList()
    }

    private fun getAllFunctionNamesNoCatching(project: Project): Collection<String> {
        return StubIndex.getInstance().getAllKeys(FUNCTION_INDEX_KEY, project)
    }

    fun getAllModules(project: Project): Collection<String> {
        return if (CATCHING)
            getAllModulesCatching(project)
        else
            getAllModulesNoCatching(project)
    }

    private fun getAllModulesCatching(project: Project): Collection<String> {
        return runCatching {
            FileBasedIndex.getInstance().getAllKeys(MODULE_INDEX_NAME, project)
        }.getOrNull() ?: emptyList()
    }

    private fun getAllModulesNoCatching(project: Project): Collection<String> {
        return FileBasedIndex.getInstance().getAllKeys(MODULE_INDEX_NAME, project)
    }

    fun getMatchingFunctionCalls(
        project: Project,
        moduleName: String,
        functionName: String
    ): Map<VirtualFile, List<ModuleFunctionCallLocation>> {
        return if (CATCHING)
            getMatchingFunctionCallsCatching(project, moduleName, functionName)
        else
            getMatchingFunctionCallsNoCatching(project, moduleName, functionName)
    }

    private fun getMatchingFunctionCallsCatching(
        project: Project,
        moduleName: String,
        functionName: String
    ): Map<VirtualFile, List<ModuleFunctionCallLocation>> {
        val map = runCatching {
            val result = hashMapOf<VirtualFile, MutableList<ModuleFunctionCallLocation>>()
            val key = ModuleFunctionCallKey(moduleName, functionName)
            FileBasedIndex.getInstance().processValues(
                MODULE_FUNCTION_CALL_INDEX_NAME,
                key,
                null,
                { file, value ->
                    result.computeIfAbsent(file) { mutableListOf() }
                        .addAll(value)
                    true
                },
                GlobalSearchScope.projectScope(project)
            )
            result
        }.getOrNull() ?: return emptyMap()
        return map
    }

    private fun getMatchingFunctionCallsNoCatching(
        project: Project,
        moduleName: String,
        functionName: String
    ): Map<VirtualFile, List<ModuleFunctionCallLocation>> {
        val result = hashMapOf<VirtualFile, MutableList<ModuleFunctionCallLocation>>()
        val key = ModuleFunctionCallKey(moduleName, functionName)
        FileBasedIndex.getInstance().processValues(
            MODULE_FUNCTION_CALL_INDEX_NAME,
            key,
            null,
            { file, value ->
                result.computeIfAbsent(file) { mutableListOf() }
                    .addAll(value)
                true
            },
            GlobalSearchScope.projectScope(project)
        )
        return result
    }

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
