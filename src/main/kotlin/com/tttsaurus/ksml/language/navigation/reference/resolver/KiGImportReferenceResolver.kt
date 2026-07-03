package com.tttsaurus.ksml.language.navigation.reference.resolver

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlModuleDecl
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

class KiGImportReferenceResolver(
    private val index: Int,
    element: PsiElement,
    rangeInElement: TextRange,
    soft: Boolean
) : PsiReferenceBase<PsiElement>(
    element,
    rangeInElement,
    soft
) {

    override fun resolve(): PsiElement? {
        val project = element.project
        if (project.isDisposed) return null

        val name = rangeInElement.substring(element.text).trim()
        if (name.isEmpty()) return null

        if (DumbService.isDumb(project)) return null

        val files = SymbolIndexEntrypoint.getMatchingFiles(project, name)

        val vFile = files.elementAtOrNull(index) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return null

        val decls = PsiTreeUtil.findChildrenOfType(psiFile, KsmlModuleDecl::class.java)
        val targetDecl = decls.firstOrNull { it.text.contains(name) } ?: return psiFile

        return targetDecl
    }
}
