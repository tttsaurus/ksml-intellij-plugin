package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.utils.GlslFileModuleImports
import com.tttsaurus.ksml.language.utils.StringExtensions.allIndicesOf

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

        val moduleName = text.substringBefore('.', missingDelimiterValue = "")
        if (moduleName.isEmpty()) return
        if (!moduleName.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) return

        val moduleNames = mutableSetOf<String>()

        val file = element.containingFile ?: return
        val langInjectionManager = InjectedLanguageManager.getInstance(element.project)
        if (langInjectionManager.isInjectedFragment(file)) {
            val host = langInjectionManager.getInjectionHost(file) ?: return
            val hostFile = host.containingFile ?: return
            if (hostFile !is KsmlFile) return

            hostFile.requiredModules.forEach {
                moduleNames.add(it.moduleName)
            }
            if (hostFile.moduleName != null) {
                moduleNames.add(hostFile.moduleName!!)
            }
        } else {
            GlslFileModuleImports.getImportedModules(file).forEach {
                moduleNames.add(it.moduleName)
            }
        }

        if (moduleName !in moduleNames) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                KsmlBundle.message("KsmlInGlsl.moduleNotImported")
            )
                .range(
                    TextRange(
                        element.textRange.startOffset,
                        element.textRange.startOffset + moduleName.length
                    )
                )
                .create()
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(
                    TextRange(
                        element.textRange.startOffset,
                        element.textRange.startOffset + moduleName.length
                    )
                )
                .textAttributes(VisualPrefabs.MODULE_USAGE_HIGHLIGHT)
                .create()
        }
    }
}
