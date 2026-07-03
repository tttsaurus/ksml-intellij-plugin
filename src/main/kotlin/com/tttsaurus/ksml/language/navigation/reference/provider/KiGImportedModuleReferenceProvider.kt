package com.tttsaurus.ksml.language.navigation.reference.provider

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.navigation.reference.resolver.KiGImportedModuleReferenceResolver
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallParser

class KiGImportedModuleReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<out PsiReference> {

        val node = element.node ?: return PsiReference.EMPTY_ARRAY
        if (node.elementType.toString() != "VARIABLE_IDENTIFIER") return PsiReference.EMPTY_ARRAY

        val text = element.text
        if (!Regex("[A-Za-z_][A-Za-z0-9_]*").matches(text))
            return PsiReference.EMPTY_ARRAY

        val ppp = element.parent?.parent?.parent ?: return PsiReference.EMPTY_ARRAY
        val moduleCall = GlslModuleCallParser.parse(ppp.text) ?: return PsiReference.EMPTY_ARRAY
        if (moduleCall.moduleName != element.text) return PsiReference.EMPTY_ARRAY

        val count = countOccurrences(moduleCall.moduleName, element, element.project)
        if (count <= 0) {
            return arrayOf(
                KiGImportedModuleReferenceResolver(
                    0,
                    moduleCall.moduleName,
                    element,
                    TextRange(0, moduleCall.moduleName.length),
                    true
                )
            )
        } else {
            val refs = ArrayList<PsiReference>()
            for (i in 0 until count) {
                refs += KiGImportedModuleReferenceResolver(
                    i,
                    moduleCall.moduleName,
                    element,
                    TextRange(0, moduleCall.moduleName.length),
                    true
                )
            }
            return refs.toTypedArray()
        }
    }

    private fun countOccurrences(moduleName: String, element: PsiElement, project: Project): Int {
        val file = element.containingFile ?: return 0

        val langInjectionManager = InjectedLanguageManager.getInstance(element.project)
        if (langInjectionManager.isInjectedFragment(file)) {
            val host = langInjectionManager.getInjectionHost(file) ?: return 0
            val hostFile = host.containingFile ?: return 0
            if (hostFile !is KsmlFile) return 0

            if (project.isDisposed) return 0
            if (DumbService.isDumb(project)) return 0
            if (moduleName.isEmpty()) return 0

            val files = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName)

            return files.size
        }

        return 0
    }
}
