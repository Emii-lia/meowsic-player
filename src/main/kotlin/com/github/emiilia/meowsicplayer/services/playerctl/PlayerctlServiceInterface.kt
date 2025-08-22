package com.github.emiilia.meowsicplayer.services.playerctl

interface PlayerctlServiceInterface {
    fun getNowPlaying(): String
    fun playPause(): String
    fun next(): String
    fun previous(): String
    fun getStatus(): String = ""
}