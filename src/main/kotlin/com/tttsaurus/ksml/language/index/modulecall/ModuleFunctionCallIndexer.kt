package com.tttsaurus.ksml.language.index.modulecall

import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import com.tttsaurus.ksml.language.utils.glsl.GlslModuleCallScanner

class ModuleFunctionCallIndexer : DataIndexer<
        ModuleFunctionCallKey,
        MutableList<ModuleFunctionCallLocation>,
        FileContent> {

    override fun map(inputData: FileContent):
            Map<ModuleFunctionCallKey, MutableList<ModuleFunctionCallLocation>> {

        val result = hashMapOf<ModuleFunctionCallKey, MutableList<ModuleFunctionCallLocation>>()

        scan(inputData.contentAsText) { call ->
            val key = ModuleFunctionCallKey(
                call.module,
                call.function
            )
            result.computeIfAbsent(key) { mutableListOf() }
                .add(
                    ModuleFunctionCallLocation(
                        call.moduleStart,
                        call.moduleEnd,
                        call.functionStart,
                        call.functionEnd
                    )
                )
        }

        return result
    }

    private inline fun scan(text: CharSequence, consumer: (GlslModuleCallScanner.ModuleCall) -> Unit) {
        val scanner = GlslModuleCallScanner(text)
        while (true) {
            consumer(scanner.nextCall() ?: break)
        }
    }
}
