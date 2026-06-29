package com.tttsaurus.ksml.language.reference.provider

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.tttsaurus.ksml.language.reference.resolver.KiGFunctionReferenceResolver

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

        return arrayOf(
            KiGFunctionReferenceResolver(
                element,
                TextRange(0, text.length),
                false
            )
        )
    }
}