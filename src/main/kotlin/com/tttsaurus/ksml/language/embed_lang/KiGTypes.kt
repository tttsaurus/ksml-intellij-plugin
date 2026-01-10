package com.tttsaurus.ksml.language.embed_lang

import com.intellij.psi.tree.IElementType

object KiGTypes {
    val IMPORT = IElementType("IMPORT", KiGLanguage.INSTANCE) // import
    val IDENT = IElementType("IDENT", KiGLanguage.INSTANCE) // identifier
}
