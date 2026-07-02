package com.tttsaurus.ksml.language.utils.ksml

object KsmlCodeDeclMetadataParser {

    private val glRequiresRegex =
        Regex("""^\s*@gl_requires\s+(\d+)(?:\s+(\S+))?\s*$""")

    private val featureRegex =
        Regex("""^\s*@feature\s+(\S+)\s*$""")

    private val exportRegex =
        Regex("""^\s*@export\s*$""")

    private val stopRegexes = listOf(
        Regex("""^\s*@code\b.*$"""),
        Regex("""^\s*@module\b.*$"""),
        Regex("""^\s*@gl_version\b.*$"""),
        Regex("""^\s*@requires\b.*$""")
    )

    /**
     * Start offset input must be the head of the whole code decl `@code """ """`.
     */
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

            val glMatch = glRequiresRegex.matchEntire(line)
            if (funcGlVersion == null && glMatch != null) {
                funcGlVersion = glMatch.groupValues[1].toIntOrNull()
                funcGlVersionIdent = glMatch.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }
                continue
            }

            val featureMatch = featureRegex.matchEntire(line)
            if (featureRequired == null && featureMatch != null) {
                featureRequired = featureMatch.groupValues[1]
                continue
            }

            if (!isExport && exportRegex.matches(line)) {
                isExport = true
            }
        }

        return KsmlCodeDeclMetadata(
            funcGlVersion = funcGlVersion,
            funcGlVersionIdent = funcGlVersionIdent,
            isExport = isExport,
            featureRequired = featureRequired
        )
    }
}