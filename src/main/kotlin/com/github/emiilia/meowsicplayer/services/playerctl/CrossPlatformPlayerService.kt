package com.github.emiilia.meowsicplayer.services.playerctl

import com.github.emiilia.meowsicplayer.utils.Platform

object CrossPlatformPlayerService {
    private val playerService = PlayerServiceFactory.createPlayerService()

    fun getNowPlaying(): String = playerService.getNowPlaying()
    fun playPause(): String = playerService.playPause()
    fun next(): String = playerService.next()
    fun previous(): String = playerService.previous()
    fun getStatus(): String = playerService.getStatus()
    fun getCurrentPlatform(): String = Platform.os.name
}