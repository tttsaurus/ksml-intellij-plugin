package com.tttsaurus.ksml.language.editor

data class ModuleRenderInfo(
    val moduleExists: Boolean,
    val moduleName: String,
    val moduleHint: String,
    val hintOffset: Int,
    val importRange: IntRange
)
