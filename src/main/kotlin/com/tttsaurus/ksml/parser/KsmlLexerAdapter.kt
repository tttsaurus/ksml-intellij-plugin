package com.tttsaurus.ksml.parser

import com.intellij.lexer.FlexAdapter
import com.tttsaurus.ksml.grammar._KsmlLexer

class KsmlLexerAdapter : FlexAdapter(_KsmlLexer(null))
