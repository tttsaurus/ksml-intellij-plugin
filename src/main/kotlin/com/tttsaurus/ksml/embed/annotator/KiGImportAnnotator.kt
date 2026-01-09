package com.tttsaurus.ksml.embed.annotator

import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.embed.lexer.KiGTokenTypes

class KiGImportAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType != KiGTokenTypes.IMPORT) return

        val next = nextNonWhitespaceLeaf(element)

        if (next == null || next.node.elementType != KiGTokenTypes.IDENT) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Missing package name after @import"
            )
                .range(element.textRange)
                .create()
        }
    }

    private fun nextNonWhitespaceLeaf(element: PsiElement): PsiElement? {
        var leaf = PsiTreeUtil.nextLeaf(element)
        while (leaf != null && leaf.node.elementType == TokenType.WHITE_SPACE) {
            leaf = PsiTreeUtil.nextLeaf(leaf)
        }
        return leaf
    }
}
