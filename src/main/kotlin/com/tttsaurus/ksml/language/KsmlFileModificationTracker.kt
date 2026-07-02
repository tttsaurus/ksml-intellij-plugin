package com.tttsaurus.ksml.language

import com.intellij.openapi.util.SimpleModificationTracker

class KsmlFileModificationTracker : SimpleModificationTracker() {

    /**
     * Manually invalidate caches for the KSML file.
     */
    fun invalidate() {
        incModificationCount()
    }
}
