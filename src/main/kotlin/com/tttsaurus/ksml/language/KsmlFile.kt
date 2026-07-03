package com.tttsaurus.ksml.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.tttsaurus.ksml.language.utils.ksml.KsmlCodeDeclLocationData
import com.tttsaurus.ksml.language.utils.ksml.KsmlCodeDeclLocationDataParser
import com.tttsaurus.ksml.language.utils.ksml.KsmlFileRequiredModule
import com.tttsaurus.ksml.language.utils.ksml.KsmlFileRequiredModuleParser
import com.tttsaurus.ksml.language.utils.ksml.KsmlModuleMetadata
import com.tttsaurus.ksml.language.utils.ksml.KsmlModuleMetadataParser
import javax.swing.Icon

class KsmlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KsmlLanguage.INSTANCE) {

    val modificationTracker = KsmlFileModificationTracker()

    private val metadata: KsmlModuleMetadata
        get() = CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                KsmlModuleMetadataParser.parse(this),
                this, modificationTracker
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

    val requiredModules: List<KsmlFileRequiredModule>
        get() = CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                KsmlFileRequiredModuleParser.parse(this),
                this, modificationTracker
            )
        }

    val codeDeclLocations: List<KsmlCodeDeclLocationData>
        get() = CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                KsmlCodeDeclLocationDataParser.parse(this),
                this, modificationTracker
            )
        }

    override fun toString(): String = viewProvider.virtualFile.name

    override fun getFileType(): FileType = KsmlFileTypeDef()

    override fun getIcon(flags: Int): Icon = KsmlIcons.FILE
}
