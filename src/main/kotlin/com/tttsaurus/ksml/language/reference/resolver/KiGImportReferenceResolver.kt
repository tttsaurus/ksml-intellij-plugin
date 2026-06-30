package com.tttsaurus.ksml.language.reference.resolver

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import com.tttsaurus.ksml.grammar.psi.KsmlModuleDecl
import com.tttsaurus.ksml.language.index.MODULE_INDEX_NAME

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

        val files = runCatching {
            FileBasedIndex.getInstance().getContainingFiles(
                MODULE_INDEX_NAME,
                name,
                GlobalSearchScope.projectScope(project)
            )
        }.getOrNull() ?: return null

        val vFile = files.elementAtOrNull(index) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return null

        val decls = PsiTreeUtil.findChildrenOfType(psiFile, KsmlModuleDecl::class.java)
        val targetDecl = decls.firstOrNull { it.text.contains(name) } ?: return psiFile

        return targetDecl
    }
}
