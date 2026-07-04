package com.tttsaurus.ksml.language.utils

object StringExtensions {

    fun String.allIndicesOf(sub: String): List<Int> {
        val result = mutableListOf<Int>()
        var index = indexOf(sub)

        while (index >= 0) {
            result.add(index)
            index = indexOf(sub, index + 1)
        }

        return result
    }

    /**
     * Returns 0 to 1000: 1000 for the best match; 0 for the worst match.
     */
    fun String.fuzzyMatchScore(target: String): Int {
        val q = lowercase()
        val t = target.lowercase()

        return when {
            t == q -> 1000
            t.startsWith(q) -> 800
            t.contains(q) -> 600
            isSubsequenceOf(t) -> 400
            else -> -1
        }
    }

    private fun String.isSubsequenceOf(target: String): Boolean {
        var i = 0
        for (c in target) {
            if (i < length && lowercase()[i] == c.lowercaseChar()) {
                i++
            }
        }
        return i == length
    }
}
