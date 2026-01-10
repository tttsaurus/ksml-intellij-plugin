package com.tttsaurus.ksml.language.embed

import com.intellij.psi.tree.IFileElementType

class KiGFileElementType : IFileElementType(KiGLanguage.INSTANCE) {
    companion object {
        val INSTANCE = KiGFileElementType()
    }
}
