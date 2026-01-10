package com.tttsaurus.ksml.language.parser

import com.intellij.lexer.FlexAdapter
import com.tttsaurus.ksml.grammar.__KsmlLexer

class KsmlLexerAdapter : FlexAdapter(__KsmlLexer(null))
