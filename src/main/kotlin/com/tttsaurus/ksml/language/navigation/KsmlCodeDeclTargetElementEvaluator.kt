package com.tttsaurus.ksml.language.navigation

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

class KsmlCodeDeclTargetElementEvaluator : TargetElementEvaluatorEx2() {

    override fun getNamedElement(element: PsiElement): PsiElement? {
        val codeDecl = PsiTreeUtil.getParentOfType(element, KsmlCodeDecl::class.java)
        if (codeDecl != null) {
            if (element.node.startOffset <= codeDecl.codeBlock.node.startOffset) {
                return codeDecl
            }
        }
        return null
    }
}
