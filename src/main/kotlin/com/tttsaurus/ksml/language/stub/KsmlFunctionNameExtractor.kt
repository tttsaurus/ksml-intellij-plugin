package com.tttsaurus.ksml.language.stub

object KsmlFunctionNameExtractor {
    private val tripleQuoteRegex = Regex("\"\"\"([\\s\\S]*?)\"\"\"")

    private val blockCommentRegex = Regex("/\\*[\\s\\S]*?\\*/")
    private val lineCommentRegex = Regex("//[^\\n]*")

    private val functionRegex = Regex(
        """
        (?xm)
        ^
        \s*
        (?:const\s+)?
        [A-Za-z_][A-Za-z0-9_]*
        \s+
        ([A-Za-z_][A-Za-z0-9_]*)
        \s*
        \([^)]*\)
        \s*
        \{
        """.trimIndent()
    )

    fun extractFromCodeBlockTokenText(text: String): String? {
        val code = tripleQuoteRegex.find(text)?.groupValues?.getOrNull(1) ?: text
        val noComments = code
            .replace(blockCommentRegex, "")
            .replace(lineCommentRegex, "")
        return functionRegex.find(noComments)?.groupValues?.getOrNull(1)
    }
}
