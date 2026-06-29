package com.tttsaurus.ksml.language.reference.resolver

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY

class KiGFunctionReferenceResolver(
    element: PsiElement,
    range: TextRange,
    soft: Boolean
) : PsiReferenceBase<PsiElement>(
    element,
    range,
    soft
) {

    override fun resolve(): PsiElement? {
        val project = element.project
        if (project.isDisposed) return null

        if (DumbService.isDumb(project)) return null

        val functionName = rangeInElement.substring(element.text)

        val decls = StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            functionName,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )

        return decls.firstOrNull()
    }
}
