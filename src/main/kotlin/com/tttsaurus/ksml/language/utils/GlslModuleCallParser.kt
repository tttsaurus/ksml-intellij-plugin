package com.tttsaurus.ksml.language.utils

object GlslModuleCallParser {

    data class ModuleCall(
        val moduleName: String,
        val functionName: String,
        val arguments: List<String>
    )

    private val moduleCallRegex =
        Regex("""^\s*([A-Za-z_]\w*)\.([A-Za-z_]\w*)\s*\(([\s\S]*)\)\s*$""")

    /**
     * A module call in the form of `module.func()` is expected.
     */
    fun parse(text: String): ModuleCall? {
        val match = moduleCallRegex.matchEntire(text)
            ?: return null

        return ModuleCall(
            moduleName = match.groupValues[1],
            functionName = match.groupValues[2],
            arguments = splitArguments(match.groupValues[3])
        )
    }

    private fun splitArguments(text: String): List<String> {
        if (text.isBlank()) {
            return emptyList()
        }

        val result = mutableListOf<String>()
        val current = StringBuilder()

        var parenDepth = 0
        var bracketDepth = 0
        var braceDepth = 0

        var inString = false
        var escaped = false

        for (c in text) {
            if (inString) {
                current.append(c)

                if (escaped) {
                    escaped = false
                    continue
                }

                when (c) {
                    '\\' -> escaped = true
                    '"' -> inString = false
                }

                continue
            }

            when (c) {
                '"' -> {
                    inString = true
                    current.append(c)
                }

                '(' -> {
                    parenDepth++
                    current.append(c)
                }

                ')' -> {
                    parenDepth--
                    current.append(c)
                }

                '[' -> {
                    bracketDepth++
                    current.append(c)
                }

                ']' -> {
                    bracketDepth--
                    current.append(c)
                }

                '{' -> {
                    braceDepth++
                    current.append(c)
                }

                '}' -> {
                    braceDepth--
                    current.append(c)
                }

                ',' -> {
                    if (parenDepth == 0 &&
                        bracketDepth == 0 &&
                        braceDepth == 0
                    ) {
                        result.add(current.toString().trim())
                        current.clear()
                    } else {
                        current.append(c)
                    }
                }

                else -> current.append(c)
            }
        }

        val last = current.toString().trim()
        if (last.isNotEmpty()) {
            result.add(last)
        }

        return result
    }
}
