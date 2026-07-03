package com.tttsaurus.ksml.language.embedded.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.utils.ModuleFunctionCallGlVersionChecker
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallParser

class GlslModuleFuncCallAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "VARIABLE_IDENTIFIER") return

        val text = element.text
        if (!Regex("[A-Za-z_][A-Za-z0-9_]*").matches(text)) return

        val parentText = element.parent?.text ?: ""
        if (!parentText.contains('(') && !parentText.contains(')')) return

        val ppp = element.parent?.parent?.parent ?: return
        val moduleCall = GlslModuleCallParser.parse(ppp.text) ?: return

        val strictDefs = findFunctionDef(
            moduleCall.moduleName,
            moduleCall.functionName,
            moduleCall.arguments,
            element.project,
            true
        )

        val looseDefs = findFunctionDef(
            moduleCall.moduleName,
            moduleCall.functionName,
            moduleCall.arguments,
            element.project,
            false
        )

        if (looseDefs.isEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.functionDefNotFound")
            )
                .range(element.textRange)
                .create()
        }

        if (!strictDefs.isEmpty()) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(VisualPrefabs.MODULE_FUNC_CALL_HIGHLIGHT)
                .create()
            if (strictDefs.size > 1) {
                holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    KsmlBundle.message("KsmlInGlsl.functionDefUnclear")
                )
                    .textAttributes(VisualPrefabs.FUNCTION_CALL_UNCLEAR_DEF)
                    .range(element.textRange)
                    .create()
            } else {
                // is exported check

                val matchingDef = strictDefs[0]
                val file = element.containingFile ?: return

                if (!matchingDef.isExport && !InjectedLanguageManager
                        .getInstance(element.project)
                        .isInjectedFragment(file)
                ) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        KsmlBundle.message("KsmlInGlsl.functionNotExported")
                    )
                        .range(element.textRange)
                        .create()
                }

                // gl version check

                val result = ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(
                    element.project,
                    file,
                    matchingDef
                )

                if (!result.first) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        KsmlBundle.message(
                            "KsmlInGlsl.functionCallGlRequirementNotMet",
                            result.second?.requiredGlVersionString ?: "UNKNOWN_GL_VERSION",
                            result.second?.envGlVersionString ?: "UNKNOWN_GL_VERSION"
                        )
                    )
                        .range(element.textRange)
                        .create()
                }
            }
        }
    }

    private fun findFunctionDef(
        moduleName: String,
        functionName: String,
        funcCallArgs: List<String>,
        project: Project,
        matchArgs: Boolean,
    ): List<KsmlCodeDecl> {

        if (project.isDisposed) return emptyList()
        if (DumbService.isDumb(project)) return emptyList()

        if (functionName.isEmpty()) return emptyList()

        val decls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, functionName)

        val result = mutableListOf<KsmlCodeDecl>()

        decls.forEach {
            if (it.moduleName == moduleName) {
                if (!matchArgs || it.params?.size == funcCallArgs.size) {
                    result += it
                }
            }
        }

        return result
    }
}
