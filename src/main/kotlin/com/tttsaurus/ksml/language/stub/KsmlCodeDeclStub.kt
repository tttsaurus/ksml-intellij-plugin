package com.tttsaurus.ksml.language.stub

import com.intellij.psi.stubs.StubElement
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

interface KsmlCodeDeclStub : StubElement<KsmlCodeDecl> {
    val functionName: String?
}
