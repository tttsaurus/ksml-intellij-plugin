package com.tttsaurus.ksml.language.navigation

import com.intellij.codeInsight.codeVision.CodeVisionAnchorKind
import com.intellij.codeInsight.codeVision.CodeVisionEntry
import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.codeVision.ui.model.ClickableRichTextCodeVisionEntry
import com.intellij.codeInsight.codeVision.ui.model.richText.RichText
import com.intellij.codeInsight.hints.codeVision.DaemonBoundCodeVisionProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SyntaxTraverser
import java.awt.event.MouseEvent

abstract class KsmlCodeVisionProviderBase : DaemonBoundCodeVisionProvider {

    abstract fun acceptsFile(file: PsiFile): Boolean

    abstract fun acceptsElement(element: PsiElement): Boolean

    abstract fun getHint(element: PsiElement, file: PsiFile): String?

    abstract fun handleClick(
        editor: Editor,
        element: PsiElement,
        event: MouseEvent?
    )

    override fun computeForEditor(
        editor: Editor,
        file: PsiFile
    ): List<Pair<TextRange, CodeVisionEntry>> {

        if (!acceptsFile(file)) {
            return emptyList()
        }

        val result = ArrayList<Pair<TextRange, CodeVisionEntry>>()

        for (element in SyntaxTraverser.psiTraverser(file)) {
            if (!acceptsElement(element)) continue
            val hint = getHint(element, file) ?: continue
            val pointer = SmartPointerManager.createPointer(element)
            val textRange = element.textRange

            // experimental
//            val textRange = InlayHintsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element)

            result += textRange to ClickableRichTextCodeVisionEntry(
                providerId = id,
                text = RichText(hint),
                onClick = { mouseEvent, editor ->
                    pointer.element?.let { psiElement ->
                        handleClick(editor, psiElement, mouseEvent)
                    }
                }
            )
        }

        return result
    }

    override val defaultAnchor = CodeVisionAnchorKind.Top

    override val relativeOrderings = emptyList<CodeVisionRelativeOrdering>()
}
