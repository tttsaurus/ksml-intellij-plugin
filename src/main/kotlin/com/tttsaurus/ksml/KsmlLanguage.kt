package com.tttsaurus.ksml

import com.intellij.lang.Language

object KsmlLanguage : Language("KSML") {
    private fun readResolve(): Any = KsmlLanguage
}
