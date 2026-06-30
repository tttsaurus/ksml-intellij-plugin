package com.tttsaurus.ksml.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.tttsaurus.ksml.language.metadata.KsmlModuleMetadata
import com.tttsaurus.ksml.language.metadata.KsmlModuleMetadataParser
import javax.swing.Icon

class KsmlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KsmlLanguage.INSTANCE) {

    private val metadata: KsmlModuleMetadata
        get() = CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                KsmlModuleMetadataParser.parse(this),
                this
            )
        }

    val moduleName
        get() = metadata.moduleName

    val moduleFileName
        get() = metadata.moduleFileName

    val moduleGlVersion
        get() = metadata.glVersion

    val moduleGlVersionIdent
        get() = metadata.glVersionIdent

    override fun toString(): String = viewProvider.virtualFile.name

    override fun getFileType(): FileType = KsmlFileTypeDef()

    override fun getIcon(flags: Int): Icon = KsmlIcons.FILE
}
