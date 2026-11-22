package com.tttsaurus.ksml.language.analysis.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.grammar.psi.KsmlGlRequiresDecl
import com.tttsaurus.ksml.grammar.psi.KsmlGlVersionDecl
import com.tttsaurus.ksml.grammar.psi.KsmlRequiresDecl
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionDiagnostics
import com.tttsaurus.ksml.language.utils.glsl.GlslVersionRequirement

class KsmlMetaDirectiveAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
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
}
