package com.tttsaurus.ksml.language.metadata

data class KsmlCodeDeclMetadata(
    val funcGlVersion: Int?,
    val funcGlVersionIdent: String?,
    val isExport: Boolean,
    val featureRequired: String?
)
