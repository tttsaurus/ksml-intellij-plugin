package com.tttsaurus.ksml.language.metadata

data class KsmlModuleMetadata(
    val moduleName: String?,
    val moduleFileName: String,
    val glVersion: Int?,
    val glVersionIdent: String?
)
