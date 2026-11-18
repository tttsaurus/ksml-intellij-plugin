package com.tttsaurus.ksml.language.utils.ksml

import com.tttsaurus.ksml.language.KsmlFile

object KsmlModuleMetadataParser {

    private val moduleRegex =
        Regex("""(?m)^\s*@module\s+(\S+)""")

    private val glVersionRegex =
        Regex("""(?m)^[\t ]*@gl_version[\t ]+(\d+)(?:[\t ]+([a-zA-Z_][a-zA-Z0-9_]*))?""")

    fun parse(file: KsmlFile): KsmlModuleMetadata {
        val text = file.text

        val moduleMatch = moduleRegex.find(text)
        val glMatch = glVersionRegex.find(text)

        return KsmlModuleMetadata(
            moduleName = moduleMatch?.groupValues?.getOrNull(1),
            moduleFileName = file.virtualFile.name,
            glVersion = glMatch?.groupValues?.getOrNull(1)?.toIntOrNull(),
            glVersionIdent = glMatch?.groups?.get(2)?.value
        )
    }
}
