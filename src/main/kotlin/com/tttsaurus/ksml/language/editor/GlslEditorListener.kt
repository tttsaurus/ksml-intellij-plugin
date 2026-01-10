package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.util.Key
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.markup.CustomHighlighterRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Disposer
import com.tttsaurus.ksml.language.editor.renderer.BackgroundRenderer
import com.tttsaurus.ksml.language.editor.renderer.BadgeRenderer

class GlslEditorListener : EditorFactoryListener {

    private val LOGGER : Logger = Logger.getInstance(GlslEditorListener::class.java)

    init {
        LOGGER.info("GlslEditorListener Created")
    }

    private val GLSL_INLAYS_KEY: Key<MutableList<Inlay<*>>> = Key.create("GLSL_INLAYS")
    private val GLSL_BACKGROUNDS_KEY: Key<MutableList<RangeHighlighter>> = Key.create("GLSL_BACKGROUNDS")

    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        if (file.fileType.defaultExtension != "glsl") return

        disposeRenderers(editor)
        installRenderers(editor)

        LOGGER.info("GlslEditorListener: custom renderers installed")
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor

        disposeRenderers(editor)

        LOGGER.info("GlslEditorListener: custom renderers disposed")
    }

    private fun addInlay(
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

    private fun addBackground(
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

    private fun installRenderers(editor: Editor) {
        val inlayList = editor.getUserData(GLSL_INLAYS_KEY)
            ?: mutableListOf<Inlay<*>>().also { editor.putUserData(GLSL_INLAYS_KEY, it) }

        val backgroundList = editor.getUserData(GLSL_BACKGROUNDS_KEY)
            ?: mutableListOf<RangeHighlighter>().also { editor.putUserData(GLSL_BACKGROUNDS_KEY, it) }

        addInlay(
            editor,
            inlayList,
            3,
            true,
            BadgeRenderer("GL460")
        )

        addBackground(
            editor,
            backgroundList,
            4,
            10,
            HighlighterLayer.ADDITIONAL_SYNTAX,
            null,
            HighlighterTargetArea.EXACT_RANGE,
            BackgroundRenderer()
        )
    }

    private fun disposeRenderers(editor: Editor) {
        editor.getUserData(GLSL_INLAYS_KEY)?.let { list ->
            for (i in list.size - 1 downTo 0) {
                Disposer.dispose(list[i])
            }
            LOGGER.info("GlslEditorListener Internal: disposed ${list.size} inlays")
            list.clear()
        }
        editor.putUserData(GLSL_INLAYS_KEY, null)

        editor.getUserData(GLSL_BACKGROUNDS_KEY)?.let { list ->
            val markup = editor.markupModel
            for (i in list.indices.reversed()) {
                markup.removeHighlighter(list[i])
            }
            LOGGER.info("GlslEditorListener Internal: disposed ${list.size} backgrounds")
            list.clear()
        }
        editor.putUserData(GLSL_BACKGROUNDS_KEY, null)
    }
}
