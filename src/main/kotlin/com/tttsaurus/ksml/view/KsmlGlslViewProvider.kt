package com.tttsaurus.ksml.view

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.tttsaurus.ksml.GlslLanguage
import com.tttsaurus.ksml.KsmlLanguage
import org.jetbrains.annotations.Unmodifiable

class KsmlGlslViewProvider(
    manager: PsiManager,
    virtualFile: VirtualFile,
    eventSystemEnabled: Boolean) : MultiplePsiFilesPerDocumentFileViewProvider(manager, virtualFile, eventSystemEnabled) {

    override fun getBaseLanguage(): Language {
        return GlslLanguage.GLSL_LANGUAGE
    }

    override fun getLanguages(): @Unmodifiable Set<Language> {
        return setOf(GlslLanguage.GLSL_LANGUAGE, KsmlLanguage)
    }

    override fun createFile(lang: Language): PsiFile? {
        TODO()
//        return when (lang) {
//            glslLanguage ->
//            KsmlLanguage ->
//            else -> null
//        }
    }

    override fun cloneInner(file: VirtualFile): MultiplePsiFilesPerDocumentFileViewProvider {
        return KsmlGlslViewProvider(manager, file, false)
    }
}
