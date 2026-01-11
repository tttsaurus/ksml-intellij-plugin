package com.tttsaurus.ksml.language.embed_lang

import com.intellij.psi.tree.IElementType

object KiGTypes {
    val COMMENT = IElementType("COMMENT", KiGLanguage.INSTANCE)
    val IMPORT = IElementType("IMPORT", KiGLanguage.INSTANCE)
    val IDENT = IElementType("IDENT", KiGLanguage.INSTANCE)
}
