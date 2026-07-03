package com.tttsaurus.ksml.language.navigation.usage

import com.intellij.openapi.vfs.VirtualFile
import com.tttsaurus.ksml.language.index.modulecall.ModuleFunctionCallLocation

data class FunctionUsageLocation(
    val file: VirtualFile,
    val location: ModuleFunctionCallLocation
)
