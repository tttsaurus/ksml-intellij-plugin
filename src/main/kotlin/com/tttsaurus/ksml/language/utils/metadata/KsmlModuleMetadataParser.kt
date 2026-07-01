package com.tttsaurus.ksml.language.utils.metadata

import com.tttsaurus.ksml.language.KsmlFile

object KsmlModuleMetadataParser {

    private val moduleRegex =
        Regex("""(?m)^\s*@module\s+(\S+)""")

    private val glVersionRegex =
        Regex("""(?m)^\s*@gl_version\s+(\d+)(?:\s+(\S+))?""")

    fun parse(file: KsmlFile): KsmlModuleMetadata {
        val text = file.text

        val moduleMatch = moduleRegex.find(text)
        val glMatch = glVersionRegex.find(text)

        return KsmlModuleMetadata(
            moduleName = moduleMatch?.groupValues?.getOrNull(1),
            moduleFileName = file.virtualFile.name,
            glVersion = glMatch?.groupValues?.getOrNull(1)?.toIntOrNull(),
            glVersionIdent = glMatch?.groupValues?.getOrNull(2)
        )
    }
}
