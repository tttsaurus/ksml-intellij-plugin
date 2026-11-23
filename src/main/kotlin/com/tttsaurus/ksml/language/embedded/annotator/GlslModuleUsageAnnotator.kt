package com.tttsaurus.ksml.language.embedded.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.tttsaurus.ksml.KsmlBundle
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import com.tttsaurus.ksml.language.VisualPrefabs
import com.tttsaurus.ksml.language.utils.glsl.GlslFileModuleImports

class GlslModuleUsageAnnotator : Annotator {

    private val moduleCallPrefix = Regex("""^([A-Za-z_][A-Za-z0-9_]*)\.[A-Za-z_][A-Za-z0-9_]*\s*\(""")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "UNARY_EXPR") return

        val moduleName = moduleCallPrefix.find(element.text)?.groupValues?.get(1) ?: return

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
            if (SymbolIndexEntrypoint.getAllModules(element.project).contains(moduleName)) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    KsmlBundle.message("structure.moduleNotImported")
                )
                    .range(
                        TextRange(
                            element.textRange.startOffset,
                            element.textRange.startOffset + moduleName.length
                        )
                    )
                    .create()
            }
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
