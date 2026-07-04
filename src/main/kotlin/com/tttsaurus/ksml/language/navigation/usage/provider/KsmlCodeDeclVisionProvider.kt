package com.tttsaurus.ksml.language.navigation.usage.provider

import com.intellij.find.actions.ShowUsagesAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.navigation.KsmlCodeVisionProviderBase
import com.tttsaurus.ksml.language.navigation.usage.FunctionUsageEntrypoint
import java.awt.event.MouseEvent

class KsmlCodeDeclVisionProvider : KsmlCodeVisionProviderBase() {

    override val id = "ksml.code_decl.usages"

    override val name = "KSML Code Decl Usages"

    override fun acceptsFile(file: PsiFile) =
        file is KsmlFile

    override fun acceptsElement(element: PsiElement): Boolean =
        element is KsmlCodeDecl

    override fun getHint(
        element: PsiElement,
        file: PsiFile
    ): String? {

        val codeDecl = element as KsmlCodeDecl

        val usages = FunctionUsageEntrypoint.getFunctionUsages(
            file.project,
            codeDecl.moduleName ?: return null,
            codeDecl.functionName ?: return null
        )

        val filtered = usages.filter {
            !FunctionUsageEntrypoint.findReferencesViaFunctionUsage(codeDecl, it).isEmpty()
        }

        return if (filtered.isEmpty()) {
            null
        } else {
            "${filtered.size} usages"
        }
    }

    override fun handleClick(
        editor: Editor,
        element: PsiElement,
        event: MouseEvent?
    ) {
        val popupPosition = JBPopupFactory
            .getInstance()
            .guessBestPopupLocation(editor)

        ShowUsagesAction.startFindUsages(
            element,
            popupPosition,
            editor
        )
    }
}
