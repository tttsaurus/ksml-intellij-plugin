package com.tttsaurus.ksml.language

import com.intellij.psi.tree.IFileElementType

class KsmlFileElementType : IFileElementType(KsmlLanguage.INSTANCE) {
    companion object {
        val INSTANCE = KsmlFileElementType()
    }
}
