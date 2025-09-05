package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import com.github.emiilia.meowsicplayer.services.playerctl.TrackMetadata
import java.io.BufferedReader
import java.io.InputStreamReader

class PlayerctlService : PlayerctlServiceInterface {
    private var lastMetadata: TrackMetadata? = null
    private var lastMetadataTime: Long = 0
    private var lastStatus: String = ""
    private var lastStatusTime: Long = 0
    private val cacheValidityMs = 500
    private fun runCommand(vararg args: String): String {
        return try {
            val process = ProcessBuilder("playerctl", *args)
                .redirectErrorStream(false).start()
            
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.readText().trim()
            }
            
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                return ""
            }
            
            output
        } catch (_: Exception) {
            ""
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

    private fun invalidateCache() {
        lastMetadata = null
        lastMetadataTime = 0
        lastStatus = ""
        lastStatusTime = 0
    }
    
    override fun getNowPlaying(): String {
        val title = runCommand("metadata", "title")
        return title.ifBlank { "No track playing" }
    }

    override fun getMetadata(): TrackMetadata {
        val currentTime = System.currentTimeMillis()

        lastMetadata?.let { cached ->
            if (currentTime - lastMetadataTime < cacheValidityMs) {
                return cached
            }
        }
        
        return try {
            val metadataRaw = runCommand("metadata", "--format", "{{title}}|{{artist}}|{{album}}|{{mpris:artUrl}}")
            val parts = metadataRaw.split("|", limit = 4)
            
            val title = parts.getOrNull(0)?.takeIf { it.isNotBlank() } ?: "Unknown Track"
            val artist = parts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
            val album = parts.getOrNull(2)?.takeIf { it.isNotBlank() } ?: "Unknown Album"
            val albumArtRaw = parts.getOrNull(3) ?: ""
            
            val albumArt = if (albumArtRaw.isNotBlank() && isValidUrl(albumArtRaw)) {
                albumArtRaw
            } else {
                ""
            }
            
            val metadata = TrackMetadata(
                title = title,
                artist = artist,
                album = album,
                albumArtUrl = albumArt
            )

            lastMetadata = metadata
            lastMetadataTime = currentTime
            metadata
        } catch (_: Exception) {
            TrackMetadata()
        }
    }

    override fun playPause(): String {
        invalidateCache()
        return runCommand("play-pause")
    }

    override fun next(): String {
        invalidateCache()
        return runCommand("next")
    }

    override fun previous(): String {
        invalidateCache()
        return runCommand("previous")
    }

    override fun getStatus(): String {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastStatusTime < cacheValidityMs && lastStatus.isNotEmpty()) {
            return lastStatus
        }
        
        val status = runCommand("status")
        val result = status.ifBlank { "Stopped" }

        lastStatus = result
        lastStatusTime = currentTime
        
        return result
    }
}