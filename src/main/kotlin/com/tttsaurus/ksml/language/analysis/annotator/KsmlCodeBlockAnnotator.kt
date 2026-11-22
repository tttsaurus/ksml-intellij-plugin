package com.tttsaurus.ksml.language.analysis.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

class KsmlCodeBlockAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val code = element as? KsmlCodeDecl ?: return
        if (code.codeBlock.text.removeSurrounding("\"\"\"").isBlank()) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message("structure.functionCount"))
                .range(code.codeBlock.textRange)
                .create()
            return
        }

        val injected = InjectedLanguageManager.getInstance(code.project).getInjectedPsiFiles(code)
            ?.firstOrNull()?.first?.containingFile ?: return

        if (PsiTreeUtil.findChildOfType(injected, PsiErrorElement::class.java) != null) return

        val declarations = injected.children.filter {
            it !is PsiComment && it !is PsiWhiteSpace
        }
        val functions = declarations.filter {
            it.node?.elementType.toString() == "FUNCTION_DEFINITION" || it.children.any { child -> child.node?.elementType.toString() == "FUNCTION_DEFINITION" }
        }
        val key = when {
            functions.size != 1 -> "structure.functionCount"
            declarations.size != 1 -> "structure.functionOnly"
            else -> return
        }

        holder
            .newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(key))
            .range(code.codeBlock.textRange)
            .create()
    }
}
