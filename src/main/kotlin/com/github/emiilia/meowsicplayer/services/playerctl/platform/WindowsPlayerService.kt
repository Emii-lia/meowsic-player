package com.github.emiilia.meowsicplayer.services.playerctl.platform

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlServiceInterface
import java.io.BufferedReader
import java.io.InputStreamReader

class WindowsPlayerService: PlayerctlServiceInterface {
    private fun runPowerShellCommand(command: String): String {
        return try {
            val process = ProcessBuilder("powershell.exe", "-Command", command)
                .redirectErrorStream(true)
                .start()
            BufferedReader(InputStreamReader(process.inputStream))
                .readText()
                .trim()
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