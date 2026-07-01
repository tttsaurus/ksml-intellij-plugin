package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.ui.JBColor
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl
import com.tttsaurus.ksml.language.index.FUNCTION_INDEX_KEY
import com.tttsaurus.ksml.language.utils.GlslModuleCallParser
import java.awt.Color
import java.awt.Font

@Suppress("Deprecation")
private val MODULE_FUNC_CALL_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
    "GLSL_MODULE_REF_HIGHLIGHT",
    TextAttributes().apply {
        foregroundColor = JBColor(Color(123, 163, 62), Color(123, 163, 62))
        fontType = Font.ITALIC
    }
)

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

        if (functionExists(
                moduleCall.moduleName,
                moduleCall.functionName,
                moduleCall.arguments,
                element.project
            )
        ) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element.textRange)
                .textAttributes(MODULE_FUNC_CALL_HIGHLIGHT)
                .create()
        } else {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.functionDefNotFound")
            )
                .range(element.textRange)
                .create()
        }
    }

    private fun functionExists(
        moduleName: String,
        functionName: String,
        funcCallArgs: List<String>,
        project: Project
    ): Boolean {

        if (project.isDisposed) return false
        if (DumbService.isDumb(project)) return false

        if (functionName.isEmpty()) return false

        val decls = StubIndex.getElements(
            FUNCTION_INDEX_KEY,
            functionName,
            project,
            GlobalSearchScope.projectScope(project),
            KsmlCodeDecl::class.java
        )

        decls.forEach {
            if (it.moduleName == moduleName) {
                if (it.params?.size == funcCallArgs.size) {
                    return true
                }
            }
        }

        return false
    }
}
