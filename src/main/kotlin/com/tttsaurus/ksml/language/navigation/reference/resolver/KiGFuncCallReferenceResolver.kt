package com.tttsaurus.ksml.language.navigation.reference.resolver

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

class KiGFuncCallReferenceResolver(
    private val index: Int,
    private val moduleName: String,
    private val moduleCallArgs: List<String>,
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

        val decls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, functionName)

        val decl = decls.elementAtOrNull(index) ?: return null
        if (decl.moduleName == moduleName) {
            if (decl.params?.size == moduleCallArgs.size) {
                return decl
            }
        }
        return null
    }
}
