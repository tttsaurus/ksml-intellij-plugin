package com.tttsaurus.ksml.language.index.modulecall

import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput

class ModuleFunctionCallKeyDescriptor : KeyDescriptor<ModuleFunctionCallKey> {

    override fun save(
        out: DataOutput,
        value: ModuleFunctionCallKey
    ) {
        IOUtil.writeUTF(out, value.module)
        IOUtil.writeUTF(out, value.function)
    }

    override fun read(input: DataInput): ModuleFunctionCallKey {
        return ModuleFunctionCallKey(
            IOUtil.readUTF(input),
            IOUtil.readUTF(input)
        )
    }

    override fun getHashCode(value: ModuleFunctionCallKey) = value.hashCode()

    override fun isEqual(
        val1: ModuleFunctionCallKey,
        val2: ModuleFunctionCallKey
    ) = val1 == val2
}
