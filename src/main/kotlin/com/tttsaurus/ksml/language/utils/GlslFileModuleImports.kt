package com.tttsaurus.ksml.language.utils

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

object GlslFileModuleImports {

    data class ImportedModule(
        val moduleName: String,
        val range: TextRange
    )

    private val IMPORT_REGEX = Regex("""//\s*@import\s+([a-zA-Z_][a-zA-Z0-9_]*)""")

    fun getImportedModules(file: PsiFile): List<ImportedModule> {
        return CachedValuesManager.getCachedValue(file) {
            val text = file.text

            val imports = IMPORT_REGEX
                .findAll(text)
                .map { match ->
                    val group = match.groups[1]!!

                    ImportedModule(
                        moduleName = group.value,
                        range = TextRange(
                            group.range.first,
                            group.range.last + 1
                        )
                    )
                }
                .toList()

            CachedValueProvider.Result.create(
                imports,
                file
            )
        }
    }
}
