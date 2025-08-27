package com.github.emiilia.meowsicplayer.services.playerctl

import com.github.emiilia.meowsicplayer.services.playerctl.CrossPlatformPlayerService
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class PlayerctlServiceTest: BasePlatformTestCase() {
    fun testGetNowPlayingAlwaysReturnsAString() {
        val nowPlaying = CrossPlatformPlayerService.getNowPlaying()
        assertTrue(nowPlaying.isNotEmpty())
    }
    fun testPlayPauseReturnsAString() {
        val playPause = CrossPlatformPlayerService.playPause()
        assertNotNull(playPause)
    }
    fun testNextReturnsEmptyString() {
        val next = CrossPlatformPlayerService.next()
        assertNotNull(next)
    }
    fun testPreviousReturnsEmptyString() {
        val previous = CrossPlatformPlayerService.previous()
        assertNotNull(previous)
    }
    fun testGetStatusReturnsString() {
        val status = CrossPlatformPlayerService.getStatus()
        assertTrue(status.isNotEmpty())
    }
}