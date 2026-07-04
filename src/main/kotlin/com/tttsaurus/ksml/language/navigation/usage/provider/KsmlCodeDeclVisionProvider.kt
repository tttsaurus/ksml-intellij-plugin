package com.tttsaurus.ksml.language.navigation.usage.provider

import com.intellij.find.actions.ShowUsagesAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.awt.RelativePoint
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

        if (filtered.isEmpty()) return null

        var consistent = true
        val firstUsage = filtered.first()
        for (usage in filtered) {
            if (usage.file.fileType.name != firstUsage.file.fileType.name) {
                consistent = false
                break;
            }
        }

        if (consistent) {
            return "${filtered.size} usages (${firstUsage.file.fileType.defaultExtension})"
        } else {
            val ksmlUsages = filtered.filter { it.file.fileType.defaultExtension == "ksml" }
            val glslUsages = filtered.filter { it.file.fileType.defaultExtension == "glsl" }
            return "${ksmlUsages.size} usages (ksml) + ${glslUsages.size} usages (glsl)"
        }
    }

    override fun handleClick(
        editor: Editor,
        element: PsiElement,
        event: MouseEvent?
    ) {
        val visual = editor.offsetToVisualPosition(element.textOffset)
        val xy = editor.visualPositionToXY(visual)

        xy.y += editor.lineHeight

        val point = RelativePoint(
            editor.contentComponent,
            xy
        )

        ShowUsagesAction.startFindUsages(
            element,
            point,
            editor
        )
    }
}
