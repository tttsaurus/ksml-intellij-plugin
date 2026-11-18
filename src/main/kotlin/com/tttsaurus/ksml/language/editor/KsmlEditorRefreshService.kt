package com.tttsaurus.ksml.language.editor

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeAdapter
import com.intellij.psi.PsiTreeChangeEvent

@Service(Service.Level.PROJECT)
class KsmlEditorRefreshService(private val project: Project) : Disposable {

    private val editors = mutableMapOf<Editor, (() -> Unit)?>()
    private val lock = Any()
    private val pendingDocuments = mutableSetOf<Document>()
    private val pendingFiles = mutableSetOf<VirtualFile>()
    private var refreshAll = false
    @Volatile private var disposed = false

    init {
        EditorFactory.getInstance().eventMulticaster.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                val file = FileDocumentManager.getInstance().getFile(event.document) ?: return
                if (!isShaderFile(file)) return
                if (PsiDocumentManager.getInstance(project).getCachedPsiFile(event.document) == null &&
                    !ProjectFileIndex.getInstance(project).isInContent(file)) return
                request(event.document, file)
            }
        }, this)

        PsiManager.getInstance(project).addPsiTreeChangeListener(object : PsiTreeChangeAdapter() {
            private fun changed(event: PsiTreeChangeEvent) {
                val psiFile = event.file ?: event.child as? PsiFile ?: return
                if (!psiFile.isPhysical) return
                val file = psiFile.virtualFile ?: return
                if (isShaderFile(file)) request(file = file)
            }

            override fun childAdded(event: PsiTreeChangeEvent) = changed(event)
            override fun childRemoved(event: PsiTreeChangeEvent) = changed(event)
            override fun childReplaced(event: PsiTreeChangeEvent) = changed(event)
            override fun childMoved(event: PsiTreeChangeEvent) = changed(event)
            override fun childrenChanged(event: PsiTreeChangeEvent) = changed(event)
            override fun propertyChanged(event: PsiTreeChangeEvent) = changed(event)
        }, this)

        project.messageBus.connect(this).subscribe(DumbService.DUMB_MODE, object : DumbService.DumbModeListener {
            override fun exitDumbMode() = requestRefresh()
        })

        ApplicationManager.getApplication().messageBus.connect(this).subscribe(
            VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    if (events.any { event ->
                            event.path.endsWith(".ksml", ignoreCase = true) ||
                                event.file?.isDirectory == true ||
                                event.file?.fileType?.defaultExtension == "ksml" ||
                                event is VFilePropertyChangeEvent &&
                                event.propertyName == VirtualFile.PROP_NAME &&
                                event.oldValue.toString().endsWith(".ksml", ignoreCase = true)
                        }) requestRefresh()
                }
            }
        )
    }

    fun registerEditor(editor: Editor, refreshBadges: (() -> Unit)? = null) {
        if (!editors.containsKey(editor)) editors[editor] = refreshBadges
        request(editor.document, FileDocumentManager.getInstance().getFile(editor.document))
    }

    fun unregisterEditor(editor: Editor) {
        editors.remove(editor)
    }

    fun requestRefresh() = request(all = true)

    private fun request(document: Document? = null, file: VirtualFile? = null, all: Boolean = false) {
        synchronized(lock) {
            if (disposed) return
            document?.let { pendingDocuments.add(it) }
            file?.let { pendingFiles.add(it) }
            refreshAll = refreshAll || all
        }
        ApplicationManager.getApplication().invokeLater({ flush() }, ModalityState.nonModal())
    }

    private fun flush() {
        if (disposed || project.isDisposed) return
        val documents = synchronized(lock) {
            if (pendingDocuments.isEmpty() && pendingFiles.isEmpty() && !refreshAll) return
            pendingDocuments.toList().also { pendingDocuments.clear() }
        }
        val psiDocuments = PsiDocumentManager.getInstance(project)
        documents.forEach {
            if (FileDocumentManager.getInstance().getFile(it)?.isValid == true) psiDocuments.commitDocument(it)
        }
        val (all, files) = synchronized(lock) {
            val batch = (refreshAll || pendingFiles.any { it.fileType.defaultExtension == "ksml" }) to pendingFiles.toSet()
            refreshAll = false
            pendingFiles.clear()
            batch
        }
        val daemon = DaemonCodeAnalyzer.getInstance(project)
        val folding = CodeFoldingManager.getInstance(project)
        val restarted = mutableSetOf<PsiFile>()
        for ((editor, refreshBadges) in editors.toMap()) {
            if (editor.isDisposed) continue
            val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
            if (!all && virtualFile !in files) continue
            refreshBadges?.invoke()
            val file = psiDocuments.getPsiFile(editor.document) ?: continue
            if (restarted.add(file)) daemon.restart(file)
            if (file.fileType.defaultExtension == "ksml") folding.scheduleAsyncFoldingUpdate(editor)
        }
    }

    private fun isShaderFile(file: VirtualFile): Boolean {
        val extension = file.fileType.defaultExtension
        return extension == "ksml" || extension == "glsl"
    }

    override fun dispose() {
        disposed = true
        editors.clear()
        synchronized(lock) {
            pendingDocuments.clear()
            pendingFiles.clear()
        }
    }
}
