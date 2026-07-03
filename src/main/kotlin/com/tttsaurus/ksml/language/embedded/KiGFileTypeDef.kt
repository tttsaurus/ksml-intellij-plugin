package com.tttsaurus.ksml.language.embedded

import com.intellij.openapi.fileTypes.LanguageFileType
import com.tttsaurus.ksml.language.KsmlIcons

open class KiGFileTypeDef : LanguageFileType(KiGLanguage.INSTANCE) {
    override fun getName() = "KsmlInGlsl"
    override fun getDescription() = "Embedded KSML calls inside GLSL"
    override fun getDefaultExtension() = "ksmlinglsl"
    override fun getIcon() = KsmlIcons.FILE
}
