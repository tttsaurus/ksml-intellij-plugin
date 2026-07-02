package com.tttsaurus.ksml.language.reference.provider

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.reference.resolver.KiGImportedModuleReferenceResolver
import com.tttsaurus.ksml.language.utils.GlslModuleCallParser

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

        return arrayOf(
            KiGImportedModuleReferenceResolver(
                moduleCall.moduleName,
                element,
                TextRange(0, moduleCall.moduleName.length),
                true
            )
        )
    }
}
