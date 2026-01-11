package com.tttsaurus.ksml.language.editor

data class ModuleRenderInfo(
    val importHint: String,
    val hintOffset: Int,
    val importRange: IntRange
)
