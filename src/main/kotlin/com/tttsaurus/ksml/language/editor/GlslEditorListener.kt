package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.Key
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.util.concurrency.EdtExecutorService
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.editor.helper.EditorListenerHelper
import com.tttsaurus.ksml.language.editor.renderer.BackgroundRenderer
import com.tttsaurus.ksml.language.editor.renderer.BadgeRenderer
import java.util.concurrent.TimeUnit

class GlslEditorListener : GlslImportRenderEditorLogic() {

    private val GLSL_INLAYS_KEY: Key<MutableList<Inlay<*>>> =
        Key.create("GLSL_INLAYS")

    private val GLSL_BACKGROUNDS_KEY: Key<MutableList<RangeHighlighter>> =
        Key.create("GLSL_BACKGROUNDS")

    private val GLSL_DOCUMENT_LISTENER_KEY: Key<DocumentListener> =
        Key.create("GLSL_DOCUMENT_LISTENER")

    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        if (virtualFile.fileType.defaultExtension != "glsl") return

        val listener = object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                updateRenderers(editor, event)
            }
        }

        editor.putUserData(GLSL_DOCUMENT_LISTENER_KEY, listener)
        editor.document.addDocumentListener(listener)
        thisLogger().info("GlslEditorListener: document listener installed")

        EdtExecutorService.getScheduledExecutorInstance()
            .schedule({
                ApplicationManager.getApplication().invokeLater {
                    if (!editor.isDisposed) {
                        updateRenderers(editor, null, true)
                    }
                }
            }, 100, TimeUnit.MILLISECONDS)

        EdtExecutorService.getScheduledExecutorInstance()
            .schedule({
                ApplicationManager.getApplication().invokeLater {
                    if (!editor.isDisposed) {
                        updateRenderers(editor, null, true)
                    }
                }
            }, 3, TimeUnit.SECONDS)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor

        editor.getUserData(GLSL_DOCUMENT_LISTENER_KEY)?.let { listener ->
            editor.document.removeDocumentListener(listener)
        }
        editor.putUserData(GLSL_DOCUMENT_LISTENER_KEY, null)
        thisLogger().info("GlslEditorListener: document listener disposed")

        disposeRenderers(editor)

        thisLogger().info("GlslEditorListener: renderers disposed")
    }

    override fun installRenderers(editor: Editor, renderList: MutableList<ModuleRenderInfo>) {
        val inlayList = editor.getUserData(GLSL_INLAYS_KEY)
            ?: mutableListOf<Inlay<*>>()
                .also { editor.putUserData(GLSL_INLAYS_KEY, it) }

        val backgroundList = editor.getUserData(GLSL_BACKGROUNDS_KEY)
            ?: mutableListOf<RangeHighlighter>()
                .also { editor.putUserData(GLSL_BACKGROUNDS_KEY, it) }

        for (renderInfo: ModuleRenderInfo in renderList) {

            EditorListenerHelper.addInlay(
                editor,
                inlayList,
                renderInfo.hintOffset,
                BadgeRenderer(renderInfo.moduleHint)
            )

            EditorListenerHelper.addBackground(
                editor,
                backgroundList,
                renderInfo.importRange.first,
                renderInfo.importRange.last,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                if (renderInfo.moduleExists) VisualPrefabs.IMPORTED_MODULE_ATTRIBUTE else null,
                HighlighterTargetArea.EXACT_RANGE,
                BackgroundRenderer()
            )
        }
    }

    // does nothing if the user data is null
    override fun disposeRenderers(editor: Editor) {
        editor.getUserData(GLSL_INLAYS_KEY)?.let { list ->
            for (i in list.size - 1 downTo 0) {
                EditorListenerHelper.disposeInlay(list[i])
            }
            list.clear()
        }
        editor.putUserData(GLSL_INLAYS_KEY, null)

        editor.getUserData(GLSL_BACKGROUNDS_KEY)?.let { list ->
            for (i in list.indices.reversed()) {
                EditorListenerHelper.disposeBackground(editor, list[i])
            }
            list.clear()
        }
        editor.putUserData(GLSL_BACKGROUNDS_KEY, null)
    }
}
