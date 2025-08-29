package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import com.github.emiilia.meowsicplayer.services.playerctl.TrackMetadata
import java.io.BufferedReader
import java.io.InputStreamReader

class PlayerctlService : PlayerctlServiceInterface {
    private fun runCommand(vararg args: String): String {
        return try {
            val process = ProcessBuilder("playerctl", *args)
                .redirectErrorStream(false).start()
            
            val output = BufferedReader(InputStreamReader(process.inputStream))
                .readText().trim()
            
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                return ""
            }
            
            output
        } catch (_: Exception) {
            ""
        }
    }
    
    override fun getNowPlaying(): String {
        val title = runCommand("metadata", "title")
        return title.ifBlank { "No track playing" }
    }

    override fun getMetadata(): TrackMetadata {
        return try {
            val title = runCommand("metadata", "title").takeIf { it.isNotBlank() } ?: "Unknown Track"
            val artist = runCommand("metadata", "artist").takeIf { it.isNotBlank() } ?: "Unknown Artist"
            val album = runCommand("metadata", "album").takeIf { it.isNotBlank() } ?: "Unknown Album"
            val albumArtRaw = runCommand("metadata", "mpris:artUrl")
            
            val albumArt = if (albumArtRaw.isNotBlank() && isValidUrl(albumArtRaw)) {
                albumArtRaw
            } else {
                ""
            }
            
            TrackMetadata(
                title = title,
                artist = artist,
                album = album,
                albumArtUrl = albumArt
            )
        } catch (_: Exception) {
            TrackMetadata()
        }
    }
    
    private fun isValidUrl(url: String): Boolean {
        return try {
            java.net.URI.create(url)
            url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")
        } catch (_: Exception) {
            false
        }
    }

    override fun playPause(): String = runCommand("play-pause")

    override fun next() = runCommand("next")

    override fun previous() = runCommand("previous")

    override fun getStatus(): String {
        val status = runCommand("status")
        return status.ifBlank { "Stopped" }
    }
}