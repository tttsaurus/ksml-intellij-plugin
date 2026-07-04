package com.tttsaurus.ksml.language.navigation.usage.provider

import com.intellij.codeInsight.codeVision.settings.CodeVisionGroupSettingProvider

class KsmlCodeDeclVisionGroupSettingProvider : CodeVisionGroupSettingProvider {

    override val groupId: String
        get() = "ksml.code_decl.usages"

    override val groupName: String
        get() = "KSML Code Decl Usages"

    override val description: String
        get() = "Show KSML code declaration function usages."
}
