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

        val project = element.project
        if (DumbService.isDumb(project)) return

        val looseDefs = findFunctionDef(moduleCall.moduleName, moduleCall.functionName, project)
        val strictDefs = looseDefs.filter { it.params?.size == moduleCall.arguments.size }

        if (strictDefs.isEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                if (looseDefs.isEmpty()) KsmlBundle.message("function.unresolved")
                else KsmlBundle.message("function.arityMismatch", moduleCall.arguments.size)
            )
                .range(element.textRange)
                .create()
            return
        }

        val file = element.containingFile ?: return
        val visibleDefs = if (InjectedLanguageManager.getInstance(project).isInjectedFragment(file)) strictDefs
            else strictDefs.filter { it.isExport }
        if (visibleDefs.isEmpty()) {
            holder
                .newAnnotation(HighlightSeverity.ERROR, KsmlBundle.message("function.notExported"))
                .range(element.textRange)
                .create()
            return
        }

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(VisualPrefabs.MODULE_FUNC_CALL_HIGHLIGHT)
            .create()
        if (visibleDefs.size > 1) {
            holder.newAnnotation(
                HighlightSeverity.WEAK_WARNING,
                KsmlBundle.message("function.ambiguous")
            )
                .textAttributes(VisualPrefabs.FUNCTION_CALL_UNCLEAR_DEF)
                .range(element.textRange)
                .create()
        }

        val checks = visibleDefs.map {
            ModuleFunctionCallGlVersionChecker.doesFileHaveRequiredGlVersion(project, file, it)
        }

        if (checks.none { it.first }) {
            val requirements = checks.mapNotNull { it.second?.requiredGlVersionString }.distinct().joinToString(" / ")
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                KsmlBundle.message(
                    "version.functionMismatch",
                    requirements, checks.first().second?.envGlVersionString ?: "UNKNOWN_GL_VERSION"
                )
            )
                .range(element.textRange)
                .create()
        }
    }

    private fun findFunctionDef(
        moduleName: String,
        functionName: String,
        project: Project,
    ): List<KsmlCodeDecl> {

        if (project.isDisposed) return emptyList()
        if (DumbService.isDumb(project)) return emptyList()

        if (functionName.isEmpty()) return emptyList()

        val decls = SymbolIndexEntrypoint.getMatchingCodeDecls(project, functionName)

        val result = mutableListOf<KsmlCodeDecl>()

        decls.forEach {
            if (it.moduleName == moduleName) {
                result += it
            }
        }

        return result
    }
}
