package com.tttsaurus.ksml.language.index.modulecall

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput

object ModuleFunctionCallLocationListExternalizer :
    DataExternalizer<MutableList<ModuleFunctionCallLocation>> {

    override fun save(
        out: DataOutput,
        value: MutableList<ModuleFunctionCallLocation>
    ) {
        out.writeInt(value.size)
        value.forEach {
            ModuleFunctionCallLocationExternalizer.save(out, it)
        }
    }

    override fun read(input: DataInput): MutableList<ModuleFunctionCallLocation> {
        val size = input.readInt()
        val result = ArrayList<ModuleFunctionCallLocation>(size)
        repeat(size) {
            result += ModuleFunctionCallLocationExternalizer.read(input)
        }
        return result
    }
}
