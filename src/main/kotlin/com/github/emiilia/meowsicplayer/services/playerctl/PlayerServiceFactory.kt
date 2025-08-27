package com.github.emiilia.meowsicplayer.services.playerctl

import com.github.emiilia.meowsicplayer.services.playerctl.platform.MacosPlayerService
import com.github.emiilia.meowsicplayer.services.playerctl.platform.PlayerctlService
import com.github.emiilia.meowsicplayer.services.playerctl.platform.WindowsPlayerService
import com.github.emiilia.meowsicplayer.utils.Platform

object PlayerServiceFactory {
    fun createPlayerService(): PlayerctlServiceInterface {
        return when (Platform.os) {
            Platform.OS.WINDOWS -> WindowsPlayerService()
            Platform.OS.MAC -> MacosPlayerService()
            Platform.OS.LINUX -> PlayerctlService()
            else -> throw UnsupportedOperationException("Unsupported OS for PlayerctlService")
        }
    }
}