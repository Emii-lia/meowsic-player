package com.github.emiilia.meowsicplayer.services.cava

interface CavaServiceInterface {
    fun start()
    fun stop()
    fun readBars(): List<Int>
}