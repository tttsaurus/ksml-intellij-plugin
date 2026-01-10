package com.tttsaurus.ksml.language.embed_lang

import com.intellij.psi.tree.IFileElementType

class KiGFileElementType : IFileElementType(KiGLanguage.INSTANCE) {
    companion object {
        val INSTANCE = KiGFileElementType()
    }
}
