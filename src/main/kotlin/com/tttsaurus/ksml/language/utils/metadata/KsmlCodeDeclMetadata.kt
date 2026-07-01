package com.tttsaurus.ksml.language.utils.metadata

data class KsmlCodeDeclMetadata(
    val funcGlVersion: Int?,
    val funcGlVersionIdent: String?,
    val isExport: Boolean,
    val featureRequired: String?
)
