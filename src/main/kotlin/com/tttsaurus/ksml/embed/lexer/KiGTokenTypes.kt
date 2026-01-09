package com.tttsaurus.ksml.embed.lexer

import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.embed.KiGLanguage

object KiGTokenTypes {
    val IMPORT = IElementType("IMPORT", KiGLanguage) // import
    val IDENT = IElementType("IDENT", KiGLanguage) // identifier
}
