package com.tttsaurus.ksml.language.embedded.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.tttsaurus.ksml.language.utils.glsl.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionDiagnostics
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement

class GlslVersionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = element as? PsiFile ?: return
        if (InjectedLanguageManager.getInstance(file.project).isInjectedFragment(file)) return
        val version = GlslFileGlVersion.getGlVersion(file) ?: return

        val range = TextRange(version.versionRange.startOffset, version.identRange?.endOffset ?: version.versionRange.endOffset)
        if (!GlslVersionDiagnostics.validate(holder, range, version.version, version.ident, ksml = false)) return
        val provided = GlslVersionRequirement.resolve(version.version, version.ident, ksml = false) ?: return

        for (module in GlslFileModuleImports.getImportedModules(file)) {
            GlslVersionDiagnostics.moduleNotice(file.project, holder, module.range, module.moduleName, provided)
        }
    }
}
