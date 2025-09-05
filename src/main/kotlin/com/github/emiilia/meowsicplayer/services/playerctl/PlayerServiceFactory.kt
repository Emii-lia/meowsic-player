package com.github.emiilia.meowsicplayer.services.playerctl

import com.github.emiilia.meowsicplayer.services.playerctl.platform.MacosPlayerService
import com.github.emiilia.meowsicplayer.services.playerctl.platform.PlayerctlService
import com.github.emiilia.meowsicplayer.services.playerctl.platform.WindowsPlayerService
import com.github.emiilia.meowsicplayer.utils.Platform
import com.intellij.openapi.diagnostic.Logger

object PlayerServiceFactory {
    private val logger = Logger.getInstance(PlayerServiceFactory::class.java)
    
    fun createPlayerService(): PlayerctlServiceInterface {
        return when (Platform.os) {
            Platform.OS.WINDOWS -> WindowsPlayerService()
            Platform.OS.MAC -> MacosPlayerService()
            Platform.OS.LINUX -> PlayerctlService()
            else -> {
                logger.warn("Unsupported OS detected: ${Platform.os}. Falling back to PlayerctlService (Linux approach).")
                PlayerctlService()
            }
        }
    }
}