package com.tttsaurus.ksml.language.utils.glsl

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiManager
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint

object GlslVersionDiagnostics {

    fun validate(
        holder: AnnotationHolder,
        range: TextRange,
        version: Int,
        identifier: String?,
        ksml: Boolean
    ): Boolean {
        val error = GlslVersionRequirement.validationError(version, identifier, ksml) ?: return true
        val argument = if (error == "version.unsupported") version.toString() else identifier.orEmpty()
        holder.newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message(error, argument)).range(range).create()
        return false
    }

    fun moduleNotice(
        project: Project,
        holder: AnnotationHolder,
        range: TextRange,
        moduleName: String,
        provided: GlslVersionRequirement
    ) {
        if (DumbService.isDumb(project)) return
        val file = SymbolIndexEntrypoint.getMatchingFiles(project, moduleName).singleOrNull() ?: return
        val module = PsiManager.getInstance(project).findFile(file) as? KsmlFile ?: return
        val required = GlslVersionRequirement.resolve(module.moduleGlVersion, module.moduleGlVersionIdent) ?: return
        if (!provided.satisfies(required)) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, KsmlBundle.message(
                "version.moduleMismatch", moduleName, required.displayName, provided.displayName
            )).range(range).create()
        }
    }
}
