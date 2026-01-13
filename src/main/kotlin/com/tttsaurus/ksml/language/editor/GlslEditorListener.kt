package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.util.Key
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import com.tttsaurus.ksml.grammar.psi.KsmlGlVersionDecl
import com.tttsaurus.ksml.language.editor.helper.EditorListenerHelper
import com.tttsaurus.ksml.language.editor.renderer.BackgroundRenderer
import com.tttsaurus.ksml.language.editor.renderer.BadgeRenderer
import com.tttsaurus.ksml.language.index.KsmlModuleIndex
import java.awt.Color
import kotlin.math.max

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

    private val LAST_RENDER_LIST_KEY: Key<List<ModuleRenderInfo>> =
        Key.create("LAST_RENDER_LIST")

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
        LOGGER.info("GlslEditorListener: document listener installed")

        updateRenderers(editor, null)
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

    private fun updateRenderers(editor: Editor, event: DocumentEvent?) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        if (virtualFile.fileType.defaultExtension != "glsl") return

        val document = editor.document
        val project = editor.project ?: return

        val lastList = editor.getUserData(LAST_RENDER_LIST_KEY)

        // forced document refresh
        var update = false
        if (event != null && lastList != null) {
            var max = -1
            for (renderInfo: ModuleRenderInfo in lastList) {
                max = max(max, renderInfo.importRange.last + 1)
            }
            if (event.offset <= max) {
                PsiDocumentManager.getInstance(project).commitDocument(document)
                update = true
            }
            if (hasImportBefore(document.text, event.offset)) {
                PsiDocumentManager.getInstance(project).commitDocument(document)
                update = true
            }
        }
        if (lastList == null) {
            PsiDocumentManager.getInstance(project).commitDocument(document)
            update = true
        }

        if (!update) return

        LOGGER.info("GlslEditorListener: update renderers")

        val renderList = mutableListOf<ModuleRenderInfo>()
        updateRenderList(project, virtualFile, renderList)

        if (lastList == renderList) return

        editor.putUserData(LAST_RENDER_LIST_KEY, renderList)

        if (renderList.isEmpty()) {
            disposeRenderers(editor)
        } else {
            disposeRenderers(editor)
            installRenderers(editor, renderList)
        }
    }

    private fun hasImportBefore(text: String, offset: Int): Boolean {
        if (offset <= 0 || offset > text.length) return false

        var i = offset - 1
        while (i >= 0 && text[i].isWhitespace()) {
            i--
        }

        val keyword = "@import"
        if (i - keyword.length + 1 < 0) return false

        for (j in keyword.indices.reversed()) {
            if (text[i] != keyword[j]) return false
            i--
        }

        return true
    }

    private fun updateRenderList(project: Project, virtualFile: VirtualFile, renderList: MutableList<ModuleRenderInfo>) {
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        val comments = PsiTreeUtil.findChildrenOfType(psiFile, PsiComment::class.java)
            .filter { it.text.contains("@import") }

        val importPrefix = "@import"
        val regex = Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")

        for (comment: PsiComment in comments) {
            val text = comment.text

            val prefixIndex = text.indexOf(importPrefix)
            if (prefixIndex == -1) continue

            val contentStartIndex = prefixIndex + importPrefix.length
            val content = text.substring(contentStartIndex)

            val matches = regex.findAll(content)
            val match = matches.firstOrNull() ?: continue

            val startInComment = contentStartIndex + match.range.first
            val endInComment = contentStartIndex + match.range.last + 1

            val moduleName = text.substring(startInComment, endInComment)
            val moduleFile = fetchModulePsiFile(project, moduleName)
            val moduleHint = fetchModuleGLVersion(moduleFile)

            renderList.add(ModuleRenderInfo(
                moduleExists = moduleFile != null,
                moduleName = moduleName,
                moduleHint = moduleHint,
                hintOffset = prefixIndex + comment.textRange.startOffset,
                importRange = IntRange(startInComment + comment.textRange.startOffset, endInComment + comment.textRange.startOffset)
            ))
        }
    }

    private fun fetchModulePsiFile(project: Project, moduleName: String): PsiFile? {
        if (DumbService.isDumb(project)) return null

        val scope = GlobalSearchScope.projectScope(project)

        val files: Collection<VirtualFile> = FileBasedIndex.getInstance()
            .getContainingFiles(KsmlModuleIndex.NAME, moduleName, scope)

        val vFile = files.firstOrNull() ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return null

        return psiFile
    }

    private fun fetchModuleGLVersion(psiFile: PsiFile?): String {
        val unknown = "UNKNOWN"
        if (psiFile == null) return unknown

        val decls = PsiTreeUtil.findChildrenOfType(psiFile, KsmlGlVersionDecl::class.java)
        val targetDecl = decls.firstOrNull() ?: return unknown

        val text = targetDecl.text

        val regex = Regex("""^@\s*gl_version\s+([1-9][0-9]{2})(?:\s+(core|compat(?:ibility)?))?\b""")
        val match = regex.find(text) ?: return unknown

        val version = match.groupValues[1].toInt()
        val profile = when (match.groupValues.getOrNull(2)) {
            "core" -> "C"
            "compat", "compatibility" -> ""
            else -> ""
        }

        return "GL$version$profile"
    }

    private val REF_ATTRIBUTE = TextAttributes().apply {
        foregroundColor = Color(80, 160, 255)
        effectType = EffectType.LINE_UNDERSCORE
        effectColor = Color(80, 160, 255)
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
                BadgeRenderer(renderInfo.moduleHint)
            )

            EditorListenerHelper.addBackground(
                editor,
                backgroundList,
                renderInfo.importRange.first,
                renderInfo.importRange.last,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                if (renderInfo.moduleExists) REF_ATTRIBUTE else null,
                HighlighterTargetArea.EXACT_RANGE,
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
