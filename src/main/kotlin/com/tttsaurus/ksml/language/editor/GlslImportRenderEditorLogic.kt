package com.tttsaurus.ksml.language.editor

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiModificationTracker
import com.tttsaurus.ksml.language.KsmlFile
import com.intellij.util.concurrency.AppExecutorUtil
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.utils.glsl.GlslProfileInferencer
import java.util.concurrent.Callable

data class ModuleRenderInfo(
    val moduleExists: Boolean,
    val moduleName: String,
    val moduleHint: String,
    val hintOffset: Int,
    val importRange: IntRange
)

abstract class GlslImportRenderEditorLogic : EditorFactoryListener {

    private data class RenderSnapshot(
        val renderList: MutableList<ModuleRenderInfo>,
        val documentStamp: Long,
        val psiStamp: Long,
        val profile: GlslProfileInferencer.GlProfile
    )

    companion object {
        private val RENDER_GENERATION_KEY = Key.create<Long>("ksml.badge.generation")
        private val LAST_RENDER_LIST_KEY = Key.create<MutableList<ModuleRenderInfo>>("LAST_RENDER_LIST")
    }

    protected fun invalidateRenderCache(editor: Editor) {
        editor.putUserData(LAST_RENDER_LIST_KEY, null)
    }

    abstract fun installRenderers(editor: Editor, renderList: MutableList<ModuleRenderInfo>)

    abstract fun disposeRenderers(editor: Editor)

    private fun updateRenderList(project: Project, psiFile: PsiFile, renderList: MutableList<ModuleRenderInfo>) {
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

        val files = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName)

        if (files.size > 1) return null

        val vFile = files.firstOrNull() ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return null

        return psiFile
    }

    private fun fetchModuleGLVersion(psiFile: PsiFile?): String {
        val unknown = "UNKNOWN"
        if (psiFile == null) return unknown

        val module = psiFile as? KsmlFile ?: return unknown
        val version = module.moduleGlVersion ?: return unknown
        val profile = GlslProfileInferencer.getProfileDescSymbol(module.project, module.moduleGlVersionIdent)
        return "GL$version$profile"
    }

    private fun buildRenderList(project: Project, document: Document): MutableList<ModuleRenderInfo> {
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document)
            ?: return mutableListOf()
        val renderList = mutableListOf<ModuleRenderInfo>()
        updateRenderList(project, psiFile, renderList)
        return renderList
    }

    private fun applyRenderList(editor: Editor, renderList: MutableList<ModuleRenderInfo>) {
        val last = editor.getUserData(LAST_RENDER_LIST_KEY)

        if (last == renderList) return

        editor.putUserData(LAST_RENDER_LIST_KEY, renderList)

        disposeRenderers(editor)

        if (renderList.isNotEmpty()) {
            installRenderers(editor, renderList)
        }
    }

    protected fun updateRenderers(editor: Editor) {
        val project = editor.project ?: return
        if (editor.isDisposed || project.isDisposed) return
        val document = editor.document
        val generation = (editor.getUserData(RENDER_GENERATION_KEY) ?: 0L) + 1L
        editor.putUserData(RENDER_GENERATION_KEY, generation)
        ReadAction.nonBlocking(Callable {
            RenderSnapshot(
                buildRenderList(project, document),
                document.modificationStamp,
                PsiModificationTracker.getInstance(project).modificationCount,
                GlslProfileInferencer.getDefaultProfile(project)
            )
        })
            .inSmartMode(project)
            .withDocumentsCommitted(project)
            .coalesceBy(this, editor)
            .expireWith(project.service<KsmlEditorRefreshService>())
            .expireWhen { editor.isDisposed || editor.getUserData(RENDER_GENERATION_KEY) != generation }
            .finishOnUiThread(ModalityState.nonModal()) { snapshot ->
                if (!editor.isDisposed && editor.getUserData(RENDER_GENERATION_KEY) == generation &&
                    document.modificationStamp == snapshot.documentStamp &&
                    PsiModificationTracker.getInstance(project).modificationCount == snapshot.psiStamp &&
                    GlslProfileInferencer.getDefaultProfile(project) == snapshot.profile) {
                    applyRenderList(editor, snapshot.renderList)
                } else if (!editor.isDisposed && editor.getUserData(RENDER_GENERATION_KEY) == generation) {
                    updateRenderers(editor)
                }
            }
            .submit(AppExecutorUtil.getAppExecutorService())
    }
}
