package com.tttsaurus.ksml.language.editor.helper

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.Disposer

class EditorListenerHelper {

    companion object {

        @JvmStatic
        fun addInlay(
            editor: Editor,
            list: MutableList<Inlay<*>>,
            offset: Int,
            renderer: EditorCustomElementRenderer
        ) {
            addInlay(
                editor,
                list,
                offset,
                true,
                renderer
            )
        }

        @JvmStatic
        fun addInlay(
            editor: Editor,
            list: MutableList<Inlay<*>>,
            offset: Int,
            relatesToPrecedingText: Boolean,
            renderer: EditorCustomElementRenderer
        ) {
            val inlay = editor.inlayModel.addInlineElement(
                offset,
                relatesToPrecedingText,
                renderer
            ) ?: return

            list.add(inlay)
        }

        @JvmStatic
        fun addBackground(
            editor: Editor,
            list: MutableList<RangeHighlighter>,
            startOffset: Int,
            endOffset: Int,
            renderer: CustomHighlighterRenderer
        ) {
            addBackground(
                editor,
                list,
                startOffset,
                endOffset,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                null,
                HighlighterTargetArea.EXACT_RANGE,
                renderer
            )
        }

        @JvmStatic
        fun addBackground(
            editor: Editor,
            list: MutableList<RangeHighlighter>,
            startOffset: Int,
            endOffset: Int,
            highlighterLayer: Int,
            textAttributes: TextAttributes?,
            highlighterTargetArea: HighlighterTargetArea,
            renderer: CustomHighlighterRenderer
        ) {
            val highlighter = editor.markupModel.addRangeHighlighter(
                startOffset,
                endOffset,
                highlighterLayer,
                textAttributes,
                highlighterTargetArea
            )

            highlighter.customRenderer = renderer
            list.add(highlighter)
        }

        @JvmStatic
        fun disposeInlay(inlay: Inlay<*>) {
            Disposer.dispose(inlay)
        }

        @JvmStatic
        fun disposeBackground(editor: Editor, highlighter: RangeHighlighter) {
            val markup = editor.markupModel
            markup.removeHighlighter(highlighter)
        }
    }
}