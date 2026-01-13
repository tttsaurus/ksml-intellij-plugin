package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex
import com.tttsaurus.ksml.grammar.psi.KsmlGlVersionDecl
import com.tttsaurus.ksml.language.index.KsmlModuleIndex
import kotlin.math.max

abstract class GlslImportRenderEditorLogic : EditorFactoryListener {
    protected val LOGGER: Logger =
        Logger.getInstance(GlslImportRenderEditorLogic::class.java)

    init {
        LOGGER.info("GlslImportRenderEditorLogic Created")
    }

    private val LAST_RENDER_LIST_KEY: Key<MutableList<ModuleRenderInfo>> =
        Key.create("LAST_RENDER_LIST")

    abstract fun installRenderers(editor: Editor, renderList: MutableList<ModuleRenderInfo>)

    abstract fun disposeRenderers(editor: Editor)

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

    private fun needsUpdate(document: Document, project: Project, lastList: MutableList<ModuleRenderInfo>?, event: DocumentEvent?): Boolean {
        var update = false

        if (event != null && lastList != null) {
            var max = -1
            for (renderInfo: ModuleRenderInfo in lastList) {
                max = max(max, renderInfo.importRange.last + 1)
            }

            if (event.offset <= max) update = true
            if (hasImportBefore(document.text, event.offset)) update = true
        }

        if (lastList == null) update = true

        return update
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

    protected fun updateRenderers(editor: Editor, event: DocumentEvent?) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return
        if (virtualFile.fileType.defaultExtension != "glsl") return

        val document = editor.document
        val project = editor.project ?: return
        val lastList = editor.getUserData(LAST_RENDER_LIST_KEY)

        if (needsUpdate(document, project, lastList, event)) {
            PsiDocumentManager.getInstance(project).commitDocument(document)
        } else {
            return
        }

        LOGGER.info("GlslImportRenderEditorLogic: update renderers")

        val newRenderList = mutableListOf<ModuleRenderInfo>()
        updateRenderList(project, virtualFile, newRenderList)

        if (lastList == newRenderList) return

        editor.putUserData(LAST_RENDER_LIST_KEY, newRenderList)

        if (newRenderList.isEmpty()) {
            disposeRenderers(editor)
        } else {
            disposeRenderers(editor)
            installRenderers(editor, newRenderList)
        }
    }
}

data class ModuleRenderInfo(
    val moduleExists: Boolean,
    val moduleName: String,
    val moduleHint: String,
    val hintOffset: Int,
    val importRange: IntRange
)
