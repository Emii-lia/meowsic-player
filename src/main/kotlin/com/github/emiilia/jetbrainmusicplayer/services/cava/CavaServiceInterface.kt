package com.github.emiilia.jetbrainmusicplayer.services.cava

interface CavaServiceInterface {
    fun start()
    fun stop()
    fun readBars(): List<Int>
}