package com.tttsaurus.ksml

import com.intellij.psi.tree.IFileElementType

class KsmlFileElementType : IFileElementType(KsmlLanguage.INSTANCE) {
    companion object {
        val INSTANCE = KsmlFileElementType()
    }
}
