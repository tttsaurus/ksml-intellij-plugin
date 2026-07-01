package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.utils.GlslFileModuleImports
import java.awt.Color
import java.awt.Font

@Suppress("Deprecation")
private val MODULE_REF_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
    "GLSL_MODULE_REF_HIGHLIGHT",
    TextAttributes().apply {
        foregroundColor = JBColor(Color(255, 139, 70), Color(255, 139, 70))
        fontType = Font.ITALIC
    }
)

class GlslModuleUsageAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "UNARY_EXPR") return

        val text = element.text
        if (!text.contains(".")) return

        if (text.allIndicesOf(".").all { index ->
                (index >= 1 && index <= text.length - 2) &&
                        (text[index - 1].isDigit() && text[index + 1].isDigit())
            }) {
            return
        }

        val qualifier = text.substringBefore('.', missingDelimiterValue = "")
        if (qualifier.isEmpty()) return
        if (!qualifier.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) return

        val file = element.containingFile ?: return

        val langInjectionManager = InjectedLanguageManager.getInstance(element.project)
        if (langInjectionManager.isInjectedFragment(file)) {
            val host = langInjectionManager.getInjectionHost(file) ?: return
            val hostFile = host.containingFile
            if (hostFile !is KsmlFile) return

            // todo
            println("module usage inside ksml glsl injected code block")

        } else {
            val importedModules = GlslFileModuleImports.getImportedModules(file)
            if (qualifier !in importedModules) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    KsmlBundle.message("KsmlInGlsl.moduleNotImported")
                )
                    .range(
                        TextRange(
                            element.textRange.startOffset,
                            element.textRange.startOffset + qualifier.length
                        )
                    )
                    .create()
            } else {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(
                        TextRange(
                            element.textRange.startOffset,
                            element.textRange.startOffset + qualifier.length
                        )
                    )
                    .textAttributes(MODULE_REF_HIGHLIGHT)
                    .create()
            }
        }
    }

    private fun String.allIndicesOf(sub: String): List<Int> {
        val result = mutableListOf<Int>()
        var index = indexOf(sub)

        while (index >= 0) {
            result.add(index)
            index = indexOf(sub, index + 1)
        }

        return result
    }
}
