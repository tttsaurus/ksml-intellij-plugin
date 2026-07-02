package com.tttsaurus.ksml.language.utils.ksml

import com.intellij.openapi.util.TextRange

data class KsmlFileRequiredModule(
    val moduleName: String,
    val range: TextRange
)
