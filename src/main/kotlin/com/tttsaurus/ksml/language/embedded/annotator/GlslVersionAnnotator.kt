package com.tttsaurus.ksml.language.embedded.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.analysis.RemoveDeclarationFix
import com.tttsaurus.ksml.language.utils.glsl.GlslFileGlVersion
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionDiagnostics
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement

class GlslVersionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val file = element as? PsiFile ?: return
        if (InjectedLanguageManager.getInstance(file.project).isInjectedFragment(file)) return

        val imports = GlslFileModuleImports.getImportedModules(file)
        val version = GlslFileGlVersion.getGlVersion(file)
        val firstCode = file.children.firstOrNull { it !is PsiComment && it !is PsiWhiteSpace }?.textRange?.startOffset
        val names = mutableSetOf<String>()
        for (module in imports) {
            val comment = PsiTreeUtil.getParentOfType(file.findElementAt(module.directiveOffset), PsiComment::class.java, false)

            fun error(key: String) {
                val annotation = holder.newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(key, module.moduleName)).range(module.range)
                if (comment != null) annotation.withFix(RemoveDeclarationFix(comment))
                annotation.create()
            }

            if (!names.add(module.moduleName)) error("structure.duplicateImport")

            if ((version != null && module.directiveOffset > version.versionRange.startOffset)
                || (firstCode != null && module.directiveOffset > firstCode)) error("structure.importFirst")

            if (!DumbService.isDumb(file.project)
                && SymbolIndexEntrypoint.getMatchingFiles(file.project, module.moduleName).isEmpty()) error("structure.unresolvedModule")
        }

        if (version == null) return

        val range = TextRange(version.versionRange.startOffset, version.identRange?.endOffset ?: version.versionRange.endOffset)
        if (!GlslVersionDiagnostics.validate(holder, range, version.version, version.ident, ksml = false)) return
        val provided = GlslVersionRequirement.resolve(version.version, version.ident, ksml = false) ?: return

        for (module in GlslFileModuleImports.getImportedModules(file)) {
            GlslVersionDiagnostics.moduleNotice(file.project, holder, module.range, module.moduleName, provided)
        }
    }
}
