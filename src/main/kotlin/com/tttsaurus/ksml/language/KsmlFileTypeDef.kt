package com.tttsaurus.ksml.language

import com.intellij.openapi.fileTypes.LanguageFileType

open class KsmlFileTypeDef : LanguageFileType(KsmlLanguage.INSTANCE) {
    override fun getName() = "KSML"
    override fun getDescription() = "Kirino shader meta language"
    override fun getDefaultExtension() = "ksml"
    override fun getIcon() = KsmlIcons.FILE
}
