package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import com.github.emiilia.meowsicplayer.services.playerctl.TrackMetadata
import java.io.BufferedReader
import java.io.InputStreamReader

class WindowsPlayerService: PlayerctlServiceInterface {
    private fun runPowerShellCommand(command: String): String {
        return try {
            val process = ProcessBuilder("powershell.exe", "-Command", command)
                .redirectErrorStream(true)
                .start()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.readText().trim()
            }
        } catch (e: Exception) {
            "Windows Media Session not available: ${e.message}"
        }
    }

    override fun getNowPlaying(): String {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}mediaProperties = ${'$'}session.TryGetMediaPropertiesAsync().GetAwaiter().GetResult()
                if (${'$'}mediaProperties.Title) {
                    Write-Output ${'$'}mediaProperties.Title
                } else {
                    Write-Output "Unknown"
                }
            } else {
                Write-Output "No active media session"
            }
        """.trimIndent()

        return runPowerShellCommand(command)
    }

    override fun playPause(): String {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}session.TryTogglePlayPauseAsync().GetAwaiter().GetResult()
                Write-Output ""
            } else {
                Write-Output "No active media session"
            }
        """.trimIndent()

        return runPowerShellCommand(command)
    }

    override fun next(): String {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}session.TrySkipNextAsync().GetAwaiter().GetResult()
                Write-Output ""
            } else {
                Write-Output "No active media session"
            }
        """.trimIndent()

        return runPowerShellCommand(command)
    }

    override fun previous(): String {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}session.TrySkipPreviousAsync().GetAwaiter().GetResult()
                Write-Output ""
            } else {
                Write-Output "No active media session"
            }
        """.trimIndent()

        return runPowerShellCommand(command)
    }

    override fun getMetadata(): TrackMetadata {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}mediaProperties = ${'$'}session.TryGetMediaPropertiesAsync().GetAwaiter().GetResult()
                ${'$'}title = if (${'$'}mediaProperties.Title) { ${'$'}mediaProperties.Title } else { "Unknown Track" }
                ${'$'}artist = if (${'$'}mediaProperties.Artist) { ${'$'}mediaProperties.Artist } else { "Unknown Artist" }
                ${'$'}album = if (${'$'}mediaProperties.AlbumTitle) { ${'$'}mediaProperties.AlbumTitle } else { "Unknown Album" }
                ${'$'}albumArt = if (${'$'}mediaProperties.Thumbnail) { ${'$'}mediaProperties.Thumbnail.ToString() } else { "" }
                
                Write-Output "${'$'}title|${'$'}artist|${'$'}album|${'$'}albumArt"
            } else {
                Write-Output "Unknown Track|Unknown Artist|Unknown Album|"
            }
        """.trimIndent()

        return try {
            val result = runPowerShellCommand(command)
            val parts = result.split("|")
            if (parts.size >= 4) {
                TrackMetadata(
                    title = parts[0].takeIf { it.isNotBlank() } ?: "Unknown Track",
                    artist = parts[1].takeIf { it.isNotBlank() } ?: "Unknown Artist",
                    album = parts[2].takeIf { it.isNotBlank() } ?: "Unknown Album",
                    albumArtUrl = parts[3]
                )
            } else {
                TrackMetadata()
            }
        } catch (_: Exception) {
            TrackMetadata()
        }
    }

    override fun getStatus(): String {
        val command = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            ${'$'}sessionManager = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()
            ${'$'}session = ${'$'}sessionManager.GetAwaiter().GetResult().GetCurrentSession()
            if (${'$'}session) {
                ${'$'}status = ${'$'}session.GetPlaybackInfo().PlaybackStatus
                if (${'$'}status -eq 4) {
                    Write-Output "Playing"
                } elseif (${'$'}status -eq 5) {
                    Write-Output "Paused"
                } else {
                    Write-Output "Stopped"
                }
            } else {
                Write-Output "Stopped"
            }
        """.trimIndent()

        return runPowerShellCommand(command)
    }
}