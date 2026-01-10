package com.tttsaurus.ksml.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import com.tttsaurus.ksml.grammar._KsmlParser

class KsmlPsiBuilder(
    builder: PsiBuilder,
    state: GeneratedParserUtilBase.ErrorState,
    parser: _KsmlParser
) : GeneratedParserUtilBase.Builder(builder, state, parser) {

    override fun getTokenType(): IElementType? {
        return super.getTokenType()
    }

    override fun getTokenText(): String? {
        return super.getTokenText()
    }
}
