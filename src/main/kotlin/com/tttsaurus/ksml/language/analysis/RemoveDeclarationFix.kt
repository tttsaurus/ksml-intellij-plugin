package com.tttsaurus.ksml.language.analysis

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.KsmlBundle

class RemoveDeclarationFix(element: PsiElement) : LocalQuickFixAndIntentionActionOnPsiElement(element) {

    override fun getText(): String = KsmlBundle.message("structure.removeDeclaration")

    override fun getFamilyName(): String = text

    override fun invoke(project: Project, file: PsiFile, editor: Editor?, startElement: PsiElement, endElement: PsiElement) {
        startElement.delete()
    }
}
