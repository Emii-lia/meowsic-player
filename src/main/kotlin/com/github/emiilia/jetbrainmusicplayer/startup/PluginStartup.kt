package com.github.emiilia.jetbrainmusicplayer.startup

import com.github.emiilia.jetbrainmusicplayer.services.cava.CavaService
import com.intellij.openapi.Disposable

class PluginStartup: Disposable {
    override fun dispose() {
        CavaService.stop()
    }
}