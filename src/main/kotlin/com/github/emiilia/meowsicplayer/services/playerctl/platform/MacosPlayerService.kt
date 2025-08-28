package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import com.github.emiilia.meowsicplayer.services.playerctl.TrackMetadata
import java.io.BufferedReader
import java.io.InputStreamReader

class MacosPlayerService: PlayerctlServiceInterface {
    private fun runAppleScript(script: String): String {
        return try {
            val process = ProcessBuilder("osascript", "-e", script)
                .redirectErrorStream(true)
                .start()
            BufferedReader(InputStreamReader(process.inputStream))
                .readText()
                .trim()
        } catch (e: Exception) {
            "AppleScript execution failed: ${e.message}"
        }
    }

    override fun getNowPlaying(): String {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        set trackName to name of current track
                        return trackName
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        set trackName to name of current track
                        return trackName
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            result = runAppleScript(spotifyScript)
        }
        
        return if (result.isBlank() || result.contains("execution failed")) {
            "Unknown"
        } else {
            result
        }
    }

    override fun playPause(): String {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        playpause
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        playpause
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            runAppleScript(spotifyScript)
        }
        
        return ""
    }

    override fun next(): String {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        next track
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        next track
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            runAppleScript(spotifyScript)
        }
        
        return ""
    }

    override fun previous(): String {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        previous track
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        previous track
                        return "success"
                    on error
                        return ""
                    end try
                else
                    return ""
                end if
            end tell
        """.trimIndent()

        val result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            runAppleScript(spotifyScript)
        }
        
        return ""
    }

    override fun getMetadata(): TrackMetadata {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        set trackName to name of current track
                        set trackArtist to artist of current track
                        set trackAlbum to album of current track
                        return trackName & "|" & trackArtist & "|" & trackAlbum & "|"
                    on error
                        return "Unknown Track|Unknown Artist|Unknown Album|"
                    end try
                else
                    return "Unknown Track|Unknown Artist|Unknown Album|"
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        set trackName to name of current track
                        set trackArtist to artist of current track
                        set trackAlbum to album of current track
                        return trackName & "|" & trackArtist & "|" & trackAlbum & "|"
                    on error
                        return "Unknown Track|Unknown Artist|Unknown Album|"
                    end try
                else
                    return "Unknown Track|Unknown Artist|Unknown Album|"
                end if
            end tell
        """.trimIndent()

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed") || result.startsWith("Unknown Track")) {
            val spotifyResult = runAppleScript(spotifyScript)
            if (spotifyResult.isNotBlank() && !spotifyResult.contains("execution failed") && !spotifyResult.startsWith("Unknown Track")) {
                result = spotifyResult
            }
        }

        return try {
            val parts = result.split("|")
            if (parts.size >= 4) {
                TrackMetadata(
                    title = parts[0].takeIf { it.isNotBlank() && it != "Unknown Track" } ?: "Unknown Track",
                    artist = parts[1].takeIf { it.isNotBlank() && it != "Unknown Artist" } ?: "Unknown Artist",
                    album = parts[2].takeIf { it.isNotBlank() && it != "Unknown Album" } ?: "Unknown Album",
                    albumArtUrl = ""
                )
            } else {
                TrackMetadata()
            }
        } catch (_: Exception) {
            TrackMetadata()
        }
    }

    override fun getStatus(): String {
        val musicScript = """
            tell application "Music"
                if it is running then
                    try
                        set state to player state
                        if state is playing then
                            return "Playing"
                        else if state is paused then
                            return "Paused"
                        else
                            return "Stopped"
                        end if
                    on error
                        return "Stopped"
                    end try
                else
                    return "Stopped"
                end if
            end tell
        """.trimIndent()

        val spotifyScript = """
            tell application "Spotify"
                if it is running then
                    try
                        set state to player state
                        if state is playing then
                            return "Playing"
                        else if state is paused then
                            return "Paused"
                        else
                            return "Stopped"
                        end if
                    on error
                        return "Stopped"
                    end try
                else
                    return "Stopped"
                end if
            end tell
        """.trimIndent()

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed") || result == "Stopped") {
            val spotifyResult = runAppleScript(spotifyScript)
            if (spotifyResult.isNotBlank() && !spotifyResult.contains("execution failed") && spotifyResult != "Stopped") {
                result = spotifyResult
            }
        }
        
        return if (result.contains("execution failed") || result.isBlank()) {
            "Stopped"
        } else {
            result
        }
    }
}