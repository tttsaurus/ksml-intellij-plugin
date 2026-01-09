package com.tttsaurus.ksml.embed

import com.intellij.lang.Language

object KiGLanguage : Language("KsmlInGlsl") {
    private fun readResolve(): Any = KiGLanguage
}
