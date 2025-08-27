package com.github.emiilia.meowsicplayer.utils

object Platform {
    enum class OS {
        WINDOWS, LINUX, MAC, UNKNOWN
    }

    val os: OS by lazy {
        val osName = System.getProperty("os.name").lowercase()
        when {
            osName.contains("win") -> OS.WINDOWS
            osName.contains("nux") || osName.contains("nix") -> OS.LINUX
            osName.contains("mac") || osName.contains("darwin") -> OS.MAC
            else -> OS.UNKNOWN
        }
    }
}