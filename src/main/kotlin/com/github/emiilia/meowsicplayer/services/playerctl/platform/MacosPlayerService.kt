package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
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

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            result = runAppleScript(spotifyScript)
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

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            result = runAppleScript(spotifyScript)
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

        var result = runAppleScript(musicScript)
        if (result.isBlank() || result.contains("execution failed")) {
            result = runAppleScript(spotifyScript)
        }
        
        return ""
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
            if (!spotifyResult.isBlank() && !spotifyResult.contains("execution failed") && spotifyResult != "Stopped") {
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