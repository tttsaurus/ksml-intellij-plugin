package com.tttsaurus.ksml.language.navigation.reference.provider

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.navigation.reference.resolver.KiGFunctionReferenceResolver
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallParser

class KiGFunctionReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(
        element: PsiElement,
        context: ProcessingContext
    ): Array<out PsiReference> {

        val node = element.node ?: return PsiReference.EMPTY_ARRAY
        if (node.elementType.toString() != "VARIABLE_IDENTIFIER") return PsiReference.EMPTY_ARRAY

        val text = element.text
        if (!Regex("[A-Za-z_][A-Za-z0-9_]*").matches(text))
            return PsiReference.EMPTY_ARRAY

        val parentText = element.parent?.text ?: ""
        if (!parentText.contains('(') && !parentText.contains(')'))
            return PsiReference.EMPTY_ARRAY

        val ppp = element.parent?.parent?.parent ?: return PsiReference.EMPTY_ARRAY
        val moduleCall = GlslModuleCallParser.parse(ppp.text) ?: return PsiReference.EMPTY_ARRAY
        val moduleName = moduleCall.moduleName
        val functionName = moduleCall.functionName

        val count = countOccurrences(functionName, element.project)
        if (count <= 0) {
            return arrayOf(
                KiGFunctionReferenceResolver(
                    0,
                    moduleName,
                    moduleCall.arguments,
                    element,
                    TextRange(0, text.length),
                    true
                )
            )
        } else {
            val refs = ArrayList<PsiReference>()
            for (i in 0 until count) {
                refs += KiGFunctionReferenceResolver(
                    i,
                    moduleName,
                    moduleCall.arguments,
                    element,
                    TextRange(0, text.length),
                    true
                )
            }
            return refs.toTypedArray()
        }
    }

    private fun countOccurrences(functionName: String, project: Project): Int {
        if (project.isDisposed) return 0
        if (DumbService.isDumb(project)) return 0

        if (functionName.isEmpty()) return 0

        val decls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, functionName)

        return decls.size
    }
}
