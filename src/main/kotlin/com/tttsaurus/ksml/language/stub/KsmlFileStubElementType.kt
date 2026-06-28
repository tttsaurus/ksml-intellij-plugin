package com.tttsaurus.ksml.language.stub

import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import com.tttsaurus.ksml.language.KsmlFile
import com.tttsaurus.ksml.language.KsmlLanguage

object KsmlFileStubElementType : IStubFileElementType<KsmlFileStub>(KsmlLanguage.INSTANCE) {

    override fun getStubVersion(): Int = 1

    override fun getExternalId(): String = "ksml.file"

    override fun serialize(stub: KsmlFileStub, dataStream: StubOutputStream) {
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KsmlFileStub {
        return KsmlFileStub(null)
    }

    override fun getBuilder(): StubBuilder {
        return object : DefaultStubBuilder() {
            override fun createStubForFile(file: PsiFile): StubElement<*> {
                return KsmlFileStub(file as? KsmlFile)
            }
        }
    }
}
