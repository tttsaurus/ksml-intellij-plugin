package com.tttsaurus.ksml.language.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.parser.GeneratedParserUtilBase.TRUE_CONDITION
import com.intellij.lang.parser.GeneratedParserUtilBase._COLLAPSE_
import com.intellij.lang.parser.GeneratedParserUtilBase.enter_section_
import com.intellij.lang.parser.GeneratedParserUtilBase.exit_section_
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.tttsaurus.ksml.grammar._KsmlParser

class KsmlParserAdapter : _KsmlParser() {
    override fun parseLight(root: IElementType, originalBuilder: PsiBuilder) {
        val state = GeneratedParserUtilBase.ErrorState()
        GeneratedParserUtilBase.ErrorState.initState(
            state,
            originalBuilder,
            root,
            emptyArray<TokenSet>()
        )

        val builder = KsmlPsiBuilder(
            originalBuilder,
            state,
            this
        )

        val marker = enter_section_(builder, 0, _COLLAPSE_, null)
        val result = parse_root_(root, builder)
        exit_section_(builder, 0, marker, root, result, true, TRUE_CONDITION)
    }
}
