package com.tttsaurus.ksml.language.navigation.usage

import com.intellij.openapi.project.Project
import com.tttsaurus.ksml.language.SymbolIndexEntrypoint
import kotlin.collections.iterator
import kotlin.collections.plusAssign

object FunctionUsageEntrypoint {

    fun getFunctionUsages(
        project: Project,
        module: String,
        function: String
    ): List<FunctionUsageLocation> {

        val map = SymbolIndexEntrypoint.getMatchingFunctionCalls(
            project,
            module,
            function)

        val result = mutableListOf<FunctionUsageLocation>()

        for (entry in map) {
            for (loc in entry.value) {
                result += FunctionUsageLocation(entry.key, loc)
            }
        }

        return result
    }
}
