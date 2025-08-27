package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import java.io.BufferedReader
import java.io.InputStreamReader

class PlayerctlService : PlayerctlServiceInterface {
    private fun runCommand(vararg args: String): String {
        return try {
            val process = ProcessBuilder("playerctl", *args)
                .redirectErrorStream(true).start()
            BufferedReader(InputStreamReader(process.inputStream))
                .readText().trim()
        } catch (e: Exception) {
            "No playerctl found. Please install it to use this feature: ${e.message}"
        }
    }
    override fun getNowPlaying(): String = runCommand("metadata", "title")

    override fun playPause(): String = runCommand("play-pause")

    override fun next() = runCommand("next")

    override fun previous() = runCommand("previous")

    override fun getStatus(): String = runCommand("status")
}