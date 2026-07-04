package com.tttsaurus.ksml.language.index

import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.intellij.util.io.VoidDataExternalizer
import java.util.regex.Pattern

val MODULE_INDEX_NAME: ID<String, Void> = ID.create("ksml.module.index")

private val MODULE_PATTERN: Pattern =
    Pattern.compile("""(?m)^\s*@module\s+([A-Za-z_][A-Za-z0-9_]*)\b""")

class KsmlModuleIndex : FileBasedIndexExtension<String, Void>() {

    override fun getName(): ID<String, Void> = MODULE_INDEX_NAME

    override fun getVersion(): Int = 1

    override fun getKeyDescriptor(): KeyDescriptor<String> =
        EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): VoidDataExternalizer =
        VoidDataExternalizer.INSTANCE

    override fun dependsOnFileContent(): Boolean = true

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        FileBasedIndex.InputFilter { file ->
            file.fileType.defaultExtension == "ksml"
        }

    override fun getIndexer(): DataIndexer<String, Void, FileContent> = DataIndexer { input ->
        val map = hashMapOf<String, Void?>()

        val text = input.contentAsText

        val m = MODULE_PATTERN.matcher(text)
        while (m.find()) {
            val name = m.group(1)?.trim().orEmpty()
            if (name.isNotEmpty()) {
                map[name] = null
            }
        }

        @Suppress("UNCHECKED_CAST")
        map as Map<String, Void>
    }
}
