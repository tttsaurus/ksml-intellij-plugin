package com.tttsaurus.ksml.language.index.modulecall

data class ModuleFunctionCallLocation(
    val moduleStart: Int,
    val moduleEnd: Int,
    val functionStart: Int,
    val functionEnd: Int
)
