package com.tttsaurus.ksml.language.utils.ksml

import com.intellij.openapi.util.TextRange
import com.tttsaurus.ksml.language.KsmlFile

object KsmlFileRequiredModuleParser {

    private val requiresRegex =
        Regex("""(?m)^\s*@requires\s+([a-zA-Z_][a-zA-Z0-9_]*)\b""")

    fun parse(file: KsmlFile): List<KsmlFileRequiredModule> {
        val text = file.text

        val modules = requiresRegex
            .findAll(text)
            .map { match ->
                val group = match.groups[1]!!

                KsmlFileRequiredModule(
                    moduleName = group.value,
                    range = TextRange(
                        group.range.first,
                        group.range.last + 1
                    )
                )
            }
            .toList()

        return modules
    }
}
