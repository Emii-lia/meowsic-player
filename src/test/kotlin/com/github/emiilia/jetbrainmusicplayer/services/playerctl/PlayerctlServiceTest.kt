package com.github.emiilia.jetbrainmusicplayer.services.playerctl

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class PlayerctlServiceTest: BasePlatformTestCase() {
    fun testGetNowPlayingAlwaysReturnsAString() {
        val nowPlaying = PlayerctlService.getNowPlaying()
        assertTrue(nowPlaying.isNotEmpty())
    }
    fun testPlayPauseReturnsAString() {
        val playPause = PlayerctlService.playPause()
        assertNotNull(playPause)
    }
    fun testNextReturnsEmptyString() {
        val next = PlayerctlService.next()
        assertNotNull(next)
    }
    fun testPreviousReturnsEmptyString() {
        val previous = PlayerctlService.previous()
        assertNotNull(previous)
    }
    fun testGetStatusReturnsString() {
        val status = PlayerctlService.getStatus()
        // Will return whether: PLaying, Paused, Stopped, or No players found
        val validStatuses = listOf("Playing", "Paused", "Stopped", "No players found")
        assertTrue(validStatuses.contains(status))
    }
}