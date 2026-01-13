package com.tttsaurus.ksml.language.embed_lang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker

class GlslExpressionAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val node = element.node ?: return
        if (node.elementType.toString() != "UNARY_EXPR") return

        val text = element.text
        if (!text.contains(".")) return

        if (text.allIndicesOf(".").all { index ->
            (index >= 1 && index <= text.length - 2) &&
                    (text[index - 1].isDigit() && text[index + 1].isDigit())
        }) { return }

        val qualifier = text.substringBefore('.', missingDelimiterValue = "")
        if (qualifier.isEmpty()) return
        if (!qualifier.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))) return

        val file = element.containingFile ?: return
        val importedModules = file.getImportedModulesCached()

        if (qualifier !in importedModules) return

        thisLogger().info("check usage: $qualifier")
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

    private val IMPORT_REGEX =
        Regex("""//\s*@import\s+([a-zA-Z_][a-zA-Z0-9_]*)""")

    private fun PsiFile.getImportedModulesCached(): Set<String> {
        val project = project

        return CachedValuesManager.getManager(project)
            .getCachedValue(this) {

                val text = text
                val imports = IMPORT_REGEX
                    .findAll(text)
                    .map { it.groupValues[1] }
                    .toSet()

                CachedValueProvider.Result.create(
                    imports,
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            }
    }
}