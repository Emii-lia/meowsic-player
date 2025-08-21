package com.github.emiilia.jetbrainmusicplayer.services.playerctl

import java.io.BufferedReader
import java.io.InputStreamReader

object PlayerctlService : PlayerctlServiceInterface {
    private fun runCommand(vararg args: String): String {
        return try {
            val process = ProcessBuilder("playerctl", *args)
                .redirectErrorStream(true).start()
            BufferedReader(InputStreamReader(process.inputStream))
                .readText().trim()
        } catch (e: Exception) {
            "No playerctl found"
        }
    }
    override fun getNowPlaying(): String = runCommand("metadata", "title")

    override fun playPause(): String = runCommand("play-pause")

    override fun next() = runCommand("next")

    override fun previous() = runCommand("previous")

    override fun getStatus(): String = runCommand("status")
}