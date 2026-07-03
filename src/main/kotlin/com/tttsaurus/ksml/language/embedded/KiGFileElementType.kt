package com.tttsaurus.ksml.language.embedded

import com.intellij.psi.tree.IFileElementType

class KiGFileElementType : IFileElementType(KiGLanguage.INSTANCE) {
    companion object {
        val INSTANCE = KiGFileElementType()
    }
}
