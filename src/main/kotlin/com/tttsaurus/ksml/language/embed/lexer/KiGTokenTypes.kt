package com.tttsaurus.ksml.language.embed.lexer

import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.language.embed.KiGLanguage

object KiGTokenTypes {
    val IMPORT = IElementType("IMPORT", KiGLanguage.INSTANCE) // import
    val IDENT = IElementType("IDENT", KiGLanguage.INSTANCE) // identifier
}
