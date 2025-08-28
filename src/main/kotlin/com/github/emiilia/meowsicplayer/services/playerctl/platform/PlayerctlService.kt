package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import com.github.emiilia.meowsicplayer.services.playerctl.TrackMetadata
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
            ""
        }
    }
    
    override fun getNowPlaying(): String = runCommand("metadata", "title")

    override fun getMetadata(): TrackMetadata {
        return try {
            val title = runCommand("metadata", "title").takeIf { it.isNotBlank() } ?: "Unknown Track"
            val artist = runCommand("metadata", "artist").takeIf { it.isNotBlank() } ?: "Unknown Artist"
            val album = runCommand("metadata", "album").takeIf { it.isNotBlank() } ?: "Unknown Album"
            val albumArt = runCommand("metadata", "mpris:artUrl").takeIf { it.isNotBlank() } ?: ""
            
            TrackMetadata(
                title = title,
                artist = artist,
                album = album,
                albumArtUrl = albumArt
            )
        } catch (e: Exception) {
            TrackMetadata() // Return default metadata if playerctl fails
        }
    }

    override fun playPause(): String = runCommand("play-pause")

    override fun next() = runCommand("next")

    override fun previous() = runCommand("previous")

    override fun getStatus(): String = runCommand("status")
}