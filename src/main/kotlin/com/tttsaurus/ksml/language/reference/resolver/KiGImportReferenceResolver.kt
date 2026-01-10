package com.tttsaurus.ksml.language.reference.resolver

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class KiGImportReferenceResolver(
    element: PsiElement,
    rangeInElement: TextRange,
    soft: Boolean
) : PsiReferenceBase<PsiElement>(
    element,
    rangeInElement,
    soft
) {

    override fun resolve(): PsiElement? {
        val name = rangeInElement.substring(element.text).trim()
        if (name.isEmpty()) return null

        val project = element.project

        thisLogger().info("KiG resolve reference: $name")

        // TODO:
        // StubIndex

        return null
    }
}