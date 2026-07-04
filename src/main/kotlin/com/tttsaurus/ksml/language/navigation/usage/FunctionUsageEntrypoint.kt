package com.tttsaurus.ksml.language.navigation.usage

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.navigation.reference.resolver.KiGFuncCallReferenceResolver
import kotlin.collections.iterator
import kotlin.collections.plusAssign

object FunctionUsageEntrypoint {

    fun getFunctionUsages(
        project: Project,
        module: String,
        function: String
    ): List<FunctionUsageLocation> {

        val map = SymbolIndexEntrypoint.getMatchingFunctionCalls(
            project,
            module,
            function)

        val result = mutableListOf<FunctionUsageLocation>()

        for (entry in map) {
            for (loc in entry.value) {
                result += FunctionUsageLocation(entry.key, loc)
            }
        }

        return result
    }

    fun findReferencesViaFunctionUsage(
        codeDecl: KsmlCodeDecl,
        usage: FunctionUsageLocation
    ): List<PsiReference> {

        val project = codeDecl.project
        val psiManager = PsiManager.getInstance(project)
        val psiFile = psiManager.findFile(usage.file) ?: return emptyList<PsiReference>()

        val injected = when (usage.file.fileType.defaultExtension) {
            "ksml" -> true
            "glsl" -> false

            else -> {
                return emptyList()
            }
        }

        val element = if (injected) {
            InjectedLanguageManager
                .getInstance(project)
                .findInjectedElementAt(
                    psiFile,
                    usage.location.functionStart
                )?.parent
        } else {
            psiFile.findElementAt(usage.location.functionStart)?.parent
        }

        if (element == null) return emptyList()

        val result = mutableListOf<PsiReference>()

        for (reference in element.references) {
            if (reference is KiGFuncCallReferenceResolver) {
                if (psiManager.areElementsEquivalent(reference.resolve(), codeDecl)) {
                    result += reference
                }
            }
        }

        return result
    }
}
