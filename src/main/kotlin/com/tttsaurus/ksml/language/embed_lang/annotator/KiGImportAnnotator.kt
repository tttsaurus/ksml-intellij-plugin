package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.embed_lang.KiGTypes

class KiGImportAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType != KiGTypes.IMPORT) return
        if (element.firstChild != null) return

        val first = nextNonWhitespaceLeaf(element)

        if (first == null || first.node.elementType != KiGTypes.IDENT) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.importError")
            )
                .range(element.textRange)
                .create()
            return
        }

        val second = nextNonWhitespaceLeaf(first)

        if (second != null && second.node.elementType == KiGTypes.IDENT) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.importTooManyIdentifiers")
            )
                .range(element.textRange)
                .create()
            return
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
