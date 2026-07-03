package com.tttsaurus.ksml.language.utils.ksml

import com.intellij.openapi.util.TextRange

data class KsmlCodeDeclLocationData(
    val functionName: String?,
    val range: TextRange
)
