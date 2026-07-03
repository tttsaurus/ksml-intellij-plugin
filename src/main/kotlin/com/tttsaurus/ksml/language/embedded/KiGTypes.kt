package com.tttsaurus.ksml.language.embedded

import com.intellij.psi.tree.IElementType

object KiGTypes {
    val COMMENT = IElementType("COMMENT", KiGLanguage.INSTANCE)
    val IMPORT = IElementType("IMPORT", KiGLanguage.INSTANCE)
    val IDENT = IElementType("IDENT", KiGLanguage.INSTANCE)
}
