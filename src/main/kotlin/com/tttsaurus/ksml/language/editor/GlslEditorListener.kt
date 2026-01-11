package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.util.Key
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.language.editor.helper.EditorListenerHelper
import com.tttsaurus.ksml.language.editor.renderer.BackgroundRenderer
import com.tttsaurus.ksml.language.editor.renderer.BadgeRenderer

class GlslEditorListener : EditorFactoryListener {

    private val LOGGER: Logger =
        Logger.getInstance(GlslEditorListener::class.java)

    init {
        LOGGER.info("GlslEditorListener Created")
    }

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
                updateRenderers(editor)
            }
        }

        editor.putUserData(GLSL_DOCUMENT_LISTENER_KEY, listener)
        editor.document.addDocumentListener(listener)
        LOGGER.info("GlslEditorListener: document listener installed")

        updateRenderers(editor)
    }

    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor

        editor.getUserData(GLSL_DOCUMENT_LISTENER_KEY)?.let { listener ->
            editor.document.removeDocumentListener(listener)
        }
        editor.putUserData(GLSL_DOCUMENT_LISTENER_KEY, null)
        LOGGER.info("GlslEditorListener: document listener diposed")

        disposeRenderers(editor)

        LOGGER.info("GlslEditorListener: renderers disposed")
    }

    private fun updateRenderers(editor: Editor) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        if (virtualFile.fileType.defaultExtension != "glsl") return

        LOGGER.info("GlslEditorListener: update renderers")

        val renderList = mutableListOf<ModuleRenderInfo>()
        updateRenderList(editor.project, virtualFile, renderList)

        if (renderList.isEmpty()) {
            disposeRenderers(editor)
        } else {
            disposeRenderers(editor)
            installRenderers(editor, renderList)
            LOGGER.info("GlslEditorListener: renderers re-installed")
        }
    }

    private fun updateRenderList(project: Project?, virtualFile: VirtualFile, renderList: MutableList<ModuleRenderInfo>) {
        if (project == null) return
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)

        val importPrefix = "@import"
        val regex = Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")

        for (comment: PsiComment in comments) {
            val text = comment.text

            var prefixIndex = text.indexOf(importPrefix)
            if (prefixIndex == -1) continue

            val contentStartIndex = prefixIndex + importPrefix.length
            val content = text.substring(contentStartIndex)

            val matches = regex.findAll(content)
            val match = matches.firstOrNull() ?: continue

            val startInComment = contentStartIndex + match.range.first
            val endInComment = contentStartIndex + match.range.last + 1

            renderList.add(ModuleRenderInfo(
                importHint = text.substring(startInComment, endInComment),
                hintOffset = prefixIndex + comment.textRange.startOffset,
                importRange = IntRange(startInComment + comment.textRange.startOffset, endInComment + comment.textRange.startOffset)
            ))
        }
    }

    private fun installRenderers(editor: Editor, renderList: MutableList<ModuleRenderInfo>) {
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
                BadgeRenderer(renderInfo.importHint)
            )

            EditorListenerHelper.addBackground(
                editor,
                backgroundList,
                renderInfo.importRange.first,
                renderInfo.importRange.last,
                BackgroundRenderer()
            )
        }
    }

    // does nothing if the user data is null
    private fun disposeRenderers(editor: Editor) {
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
