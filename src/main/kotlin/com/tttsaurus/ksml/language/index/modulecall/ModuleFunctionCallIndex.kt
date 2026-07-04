package com.tttsaurus.ksml.language.index.modulecall

import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.ID

val MODULE_FUNCTION_CALL_INDEX_NAME: ID<ModuleFunctionCallKey, MutableList<ModuleFunctionCallLocation>> =
    ID.create("ksml.modulecall.index")

class ModuleFunctionCallIndex :
    FileBasedIndexExtension<ModuleFunctionCallKey, MutableList<ModuleFunctionCallLocation>>() {

    override fun getName() = MODULE_FUNCTION_CALL_INDEX_NAME

    override fun getIndexer() = ModuleFunctionCallIndexer()

    override fun getVersion() = 1

    override fun dependsOnFileContent() = true

    override fun getKeyDescriptor() = ModuleFunctionCallKeyDescriptor()

    override fun getValueExternalizer() = ModuleFunctionCallLocationListExternalizer

    override fun getInputFilter() =
        FileBasedIndex.InputFilter { file ->
            file.fileType.defaultExtension == "ksml" ||
                    file.fileType.defaultExtension == "glsl"
        }
}
