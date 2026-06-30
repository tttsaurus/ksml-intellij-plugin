package com.tttsaurus.ksml.language.stub

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.IStubElementType
import com.tttsaurus.ksml.grammar.psi.KsmlCodeDecl

class KsmlCodeDeclStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    override val functionName: String?,
    override val moduleName: String?,
    override val moduleFileName: String?,
    override val moduleGlVersion: Int?,
    override val moduleGlVersionIdent: String?,
    override val funcGlVersion: Int?,
    override val funcGlVersionIdent: String?,
    override val isExport: Boolean,
    override val featureRequired: String?,
    override val params: List<String>?
) : StubBase<KsmlCodeDecl>(parent, elementType), KsmlCodeDeclStub
