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

    fun String.fuzzyMatch(target: String): Boolean {
        return target.contains(this)
    }
}
