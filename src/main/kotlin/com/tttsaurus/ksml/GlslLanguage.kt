package com.tttsaurus.ksml

import com.intellij.lang.Language

class GlslLanguage {
    companion object {
        val GLSL_LANGUAGE: Language by lazy {
            Language.findLanguageByID("Glsl")
                ?: error("GLSL language not found. Is the GLSL plugin enabled?")
        }
    }
}
