package com.tttsaurus.ksml.language.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

val FUNCTION_INDEX_KEY: StubIndexKey<String, KsmlCodeDecl> =
    StubIndexKey.createIndexKey("ksml.function.index")

class KsmlFunctionIndex : StringStubIndexExtension<KsmlCodeDecl>() {

    override fun getKey(): StubIndexKey<String, KsmlCodeDecl> = FUNCTION_INDEX_KEY

    override fun getVersion(): Int = 1
}
