package com.tttsaurus.ksml.language.embedded.annotator

import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.analysis.RemoveDeclarationFix
import com.tttsaurus.ksml.language.embedded.KiGTypes
import com.tttsaurus.ksml.language.embedded.psi.KiGImportDecl

class KiGImportAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is PsiFile) return
        val directive = PsiTreeUtil.findChildOfType(element, KiGImportDecl::class.java)?.firstChild ?: return

        val first = nextNonWhitespaceLeaf(directive)

        if (first == null || first.node.elementType != KiGTypes.IDENT) {
            error(holder, element, directive.textRange, "structure.missingImportName")
            return
        }

        if (!Regex("[A-Za-z_][A-Za-z_0-9]*").matches(first.text)) {
            error(holder, element, first.textRange, "structure.moduleIdentifier")
            return
        }

        val second = nextNonWhitespaceLeaf(first)

        if (second != null && second.node.elementType == KiGTypes.IDENT) {
            error(holder, element, directive.textRange, "structure.multipleImportNames")
            return
        }

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(directive.textRange)
            .textAttributes(VisualPrefabs.MODULE_IMPORT_HIGHLIGHT)
            .create()
    }

    private fun error(holder: AnnotationHolder, element: PsiElement, range: TextRange, key: String) {
        val annotation = holder.newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(key)).range(range)
        val host = InjectedLanguageManager.getInstance(element.project).getInjectionHost(element)
        if (host != null) annotation.withFix(RemoveDeclarationFix(host))
        annotation.create()
    }

    private fun nextNonWhitespaceLeaf(element: PsiElement): PsiElement? {
        var leaf = PsiTreeUtil.nextLeaf(element)
        while (leaf != null && leaf.node.elementType == TokenType.WHITE_SPACE) {
            leaf = PsiTreeUtil.nextLeaf(leaf)
        }
        return leaf
    }
}
