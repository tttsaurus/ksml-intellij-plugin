package com.tttsaurus.ksml.language.index.modulecall

import com.intellij.util.io.DataExternalizer
import java.io.DataInput
import java.io.DataOutput

object ModuleFunctionCallLocationExternalizer : DataExternalizer<ModuleFunctionCallLocation> {

    override fun save(
        out: DataOutput,
        value: ModuleFunctionCallLocation
    ) {
        out.writeInt(value.moduleStart)
        out.writeInt(value.moduleEnd)
        out.writeInt(value.functionStart)
        out.writeInt(value.functionEnd)
    }

    override fun read(input: DataInput): ModuleFunctionCallLocation {
        return ModuleFunctionCallLocation(
            input.readInt(),
            input.readInt(),
            input.readInt(),
            input.readInt()
        )
    }
}
