package com.tttsaurus.ksml.language.stub

import com.intellij.psi.stubs.StubElement
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

interface KsmlCodeDeclStub : StubElement<KsmlCodeDecl> {
    val functionName: String?
    val moduleName: String?
    val moduleFileName: String?
    val moduleGlVersion: Int?
    val moduleGlVersionIdent: String?
    val funcGlVersion: Int?
    val funcGlVersionIdent: String?
    val isExport: Boolean
    val featureRequired: String?
    val params: List<String>?
    val returnType: String?
}
