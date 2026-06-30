package com.tttsaurus.ksml.language.metadata

import com.tttsaurus.ksml.grammar.psi.*

object KsmlCodeDeclMetadataParser {

    private val stopDeclTypes = setOf(
        KsmlCodeDecl::class.java,
        KsmlModuleDecl::class.java,
        KsmlGlVersionDecl::class.java,
        KsmlRequiresDecl::class.java
    )

    fun parse(psi: KsmlCodeDecl): KsmlCodeDeclMetadata {
        var glInfoSet = false
        var funcGlVersion: Int? = null
        var funcGlVersionIdent: String? = null

        var featureInfoSet = false
        var featureRequired: String? = null

        var exportInfoSet = false
        var isExport = false

        var item = psi.parent
        while (item !is KsmlItem) {
            item = psi.parent
        }

        while (true) {
            item = item.prevSibling ?: break
            if (item !is KsmlItem) continue

            val annotation = item.firstChild ?: continue
            if (annotation !is KsmlKsmlAnnotation) continue

            val decl = item.firstChild ?: continue

            when (decl) {
                is KsmlGlRequiresDecl -> {
                    if (!glInfoSet) {
                        glInfoSet = true
                        funcGlVersion = decl.number.text.toIntOrNull()
                        funcGlVersionIdent = decl.identifier?.text
                    }
                }

                is KsmlFeatureDecl -> {
                    if (!featureInfoSet) {
                        featureInfoSet = true
                        featureRequired = decl.identifier.text
                    }
                }

                is KsmlExportDecl -> {
                    if (!exportInfoSet) {
                        exportInfoSet = true
                        isExport = true
                    }
                }

                else -> {
                    if (stopDeclTypes.any { it.isInstance(decl) }) {
                        break
                    }
                }
            }
        }

        return KsmlCodeDeclMetadata(
            funcGlVersion,
            funcGlVersionIdent,
            isExport,
            featureRequired
        )
    }
}