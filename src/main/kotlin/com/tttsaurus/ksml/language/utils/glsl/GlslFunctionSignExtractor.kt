package com.tttsaurus.ksml.language.utils.glsl

object GlslFunctionSignExtractor {

    private val tripleQuoteRegex = Regex("\"\"\"([\\s\\S]*?)\"\"\"")

    private val blockCommentRegex = Regex("/\\*[\\s\\S]*?\\*/")
    private val lineCommentRegex = Regex("//[^\\n]*")

    private val functionRegex = Regex(
        """
        (?xm)
        ^
        \s*
        (?:const\s+)?
        ([A-Za-z_][A-Za-z0-9_]*(?:\s*\[\s*\d*\s*])*)
        \s+
        ([A-Za-z_][A-Za-z0-9_]*)
        \s*
        \(([^)]*)\)
        \s*
        \{
        """.trimIndent()
    )

    private val qualifiers = setOf(
        "const",
        "in",
        "out",
        "inout"
    )

    private fun extractCode(text: String): String {
        val code = tripleQuoteRegex.find(text)?.groupValues?.getOrNull(1) ?: text
        return code
            .replace(blockCommentRegex, "")
            .replace(lineCommentRegex, "")
    }

    /**
     * A code block is expected (a code block is in the form of `""" """`; `@code """ """` is acceptable too).
     */
    fun extractReturnTypeFromCodeBlockTokenText(text: String): String? {
        val code = extractCode(text)
        return functionRegex
            .find(code)
            ?.groupValues
            ?.getOrNull(1)
    }

    /**
     * A code block is expected (a code block is in the form of `""" """`; `@code """ """` is acceptable too).
     */
    fun extractFuncNameFromCodeBlockTokenText(text: String): String? {
        val code = extractCode(text)
        return functionRegex
            .find(code)
            ?.groupValues
            ?.getOrNull(2)
    }

    /**
     * A code block is expected (a code block is in the form of `""" """`; `@code """ """` is acceptable too).
     */
    fun extractParamTypesFromCodeBlockTokenText(text: String): List<String> {
        val code = extractCode(text)

        val params = functionRegex
            .find(code)
            ?.groupValues
            ?.getOrNull(3)
            ?.trim()
            ?: return emptyList()

        if (params.isEmpty()) {
            return emptyList()
        }

        return params
            .split(',')
            .map { it.trim() }
            .mapNotNull { param ->
                val tokens = param
                    .split(Regex("\\s+"))
                    .filter { it.isNotEmpty() }

                tokens.firstOrNull { it !in qualifiers }
            }
    }
}
