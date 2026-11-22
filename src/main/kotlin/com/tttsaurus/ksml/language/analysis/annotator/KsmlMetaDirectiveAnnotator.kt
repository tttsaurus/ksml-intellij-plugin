package com.tttsaurus.ksml.language.analysis.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.grammar.psi.KsmlExportDecl
import com.tttsaurus.ksml.grammar.psi.KsmlFeatureDecl
import com.tttsaurus.ksml.grammar.psi.KsmlGlRequiresDecl
import com.tttsaurus.ksml.grammar.psi.KsmlGlVersionDecl
import com.tttsaurus.ksml.grammar.psi.KsmlRequiresDecl
import com.tttsaurus.ksml.grammar.psi.KsmlModuleDecl
import com.tttsaurus.ksml.grammar.psi.KsmlKsmlAnnotation
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.analysis.RemoveDeclarationFix
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionDiagnostics
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement

class KsmlMetaDirectiveAnnotator : Annotator {

    private fun error(holder: AnnotationHolder, range: TextRange, key: String, vararg arguments: Any) {
        holder
            .newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(key, *arguments))
            .range(range)
            .create()
    }

    private fun removable(holder: AnnotationHolder, element: PsiElement, key: String, vararg arguments: Any) {
        holder
            .newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(key, *arguments))
            .range(element.textRange)
            .withFix(RemoveDeclarationFix(element)).create()
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is KsmlFile -> checkStructure(element, holder)
            is KsmlGlVersionDecl -> GlslVersionDiagnostics.validate(
                holder,
                element.textRange,
                element.number.text.toIntOrNull() ?: -1,
                element.identifier?.text,
                ksml = true
            )
            is KsmlGlRequiresDecl -> GlslVersionDiagnostics.validate(
                holder,
                element.textRange,
                element.number.text.toIntOrNull() ?: -1,
                element.identifier?.text,
                ksml = true
            )
            is KsmlRequiresDecl -> {
                if (!DumbService.isDumb(element.project) &&
                    SymbolIndexEntrypoint.getMatchingFiles(element.project, element.identifier.text).isEmpty()) {
                    error(holder, element.identifier.textRange, "structure.unresolvedModule", element.identifier.text)
                    return
                }
                val file = element.containingFile as? KsmlFile ?: return
                val provided = GlslVersionRequirement.resolve(file.moduleGlVersion, file.moduleGlVersionIdent) ?: return
                GlslVersionDiagnostics.moduleNotice(
                    element.project,
                    holder,
                    element.identifier.textRange,
                    element.identifier.text,
                    provided
                )
            }
        }
    }

    private fun checkStructure(file: KsmlFile, holder: AnnotationHolder) {
        val declarations = PsiTreeUtil.findChildrenOfType(file, KsmlKsmlAnnotation::class.java)
            .mapNotNull { it.firstChild }
            .sortedBy { it.textRange.startOffset }

        val modules = declarations.filterIsInstance<KsmlModuleDecl>()
        val versions = declarations.filterIsInstance<KsmlGlVersionDecl>()
        val anchor = declarations.firstOrNull()?.textRange ?: TextRange(0, 0)
        if (modules.isEmpty()) error(holder, anchor, "structure.missingModule")
        if (versions.isEmpty()) error(holder, anchor, "structure.missingVersion")

        modules.drop(1).forEach {
            removable(holder, it, "structure.duplicateModule")
        }
        versions.drop(1).forEach {
            removable(holder, it, "structure.duplicateVersion")
        }
        modules.firstOrNull()?.takeIf {
            it != declarations.firstOrNull()
        }?.let {
            error(holder, it.textRange, "structure.moduleFirst")
        }

        val firstCode = declarations.filterIsInstance<KsmlCodeDecl>().firstOrNull()?.textRange?.startOffset
        val imports = mutableSetOf<String>()
        val annotations = mutableListOf<PsiElement>()
        fun flush(attached: Boolean) {
            if (!attached) annotations.forEach {
                removable(holder, it, "structure.danglingAnnotation")
            }
            for (duplicates in listOf(annotations.filterIsInstance<KsmlExportDecl>(), annotations.filterIsInstance<KsmlGlRequiresDecl>())) {
                duplicates.drop(1).forEach {
                    removable(holder, it, "structure.duplicateAnnotation")
                }
            }
            annotations.clear()
        }

        for (declaration in declarations) {
            when (declaration) {
                is KsmlExportDecl, is KsmlFeatureDecl, is KsmlGlRequiresDecl -> {
                    annotations.add(declaration)
                    if (declaration is KsmlExportDecl && declaration.identifier != null) {
                        error(holder, declaration.identifier!!.textRange, "structure.exportArgument")
                    }
                }
                is KsmlCodeDecl -> flush(true)
                else -> {
                    flush(false)
                    if (declaration is KsmlRequiresDecl) {
                        if (!imports.add(declaration.identifier.text)) {
                            removable(holder, declaration, "structure.duplicateImport", declaration.identifier.text)
                        }
                    }
                    if (firstCode != null && declaration.textRange.startOffset > firstCode
                        && (declaration is KsmlRequiresDecl || declaration is KsmlGlVersionDecl)) {
                        error(holder, declaration.textRange, "structure.beforeCode")
                    }
                }
            }
        }

        flush(false)
    }
}
