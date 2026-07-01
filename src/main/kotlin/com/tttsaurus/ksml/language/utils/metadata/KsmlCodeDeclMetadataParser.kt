package com.tttsaurus.ksml.language.utils.metadata

object KsmlCodeDeclMetadataParser {

    private val glRequiresRegex =
        Regex("""^\s*@gl_requires\s+(\d+)(?:\s+(\S+))?\s*$""")

    private val featureRegex =
        Regex("""^\s*@feature\s+(\S+)\s*$""")

    private val exportRegex =
        Regex("""^\s*@export\s*$""")

    private val stopRegexes = listOf(
        Regex("""^\s*@code\b"""),
        Regex("""^\s*@module\b"""),
        Regex("""^\s*@gl_version\b"""),
        Regex("""^\s*@requires\b""")
    )

    fun parse(codeDeclStartOffset: Int, fileContent: String): KsmlCodeDeclMetadata {
        var funcGlVersion: Int? = null
        var funcGlVersionIdent: String? = null
        var featureRequired: String? = null
        var isExport = false

        val before = fileContent.substring(0, codeDeclStartOffset)
        val lines = before.lines()

        for (line in lines.asReversed()) {
            if (stopRegexes.any { it.matches(line) }) {
                break
            }

            if (funcGlVersion == null) {
                glRequiresRegex.matchEntire(line)?.let {
                    funcGlVersion = it.groupValues[1].toIntOrNull()
                    funcGlVersionIdent = it.groupValues.getOrNull(2)?.takeIf(String::isNotBlank)
                    continue
                }
            }

            if (featureRequired == null) {
                featureRegex.matchEntire(line)?.let {
                    featureRequired = it.groupValues[1]
                    continue
                }
            }

            if (!isExport) {
                if (exportRegex.matches(line)) {
                    isExport = true
                    continue
                }
            }
        }

        return KsmlCodeDeclMetadata(
            funcGlVersion,
            funcGlVersionIdent,
            isExport,
            featureRequired
        )
    }
}
