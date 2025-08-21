package com.github.emiilia.jetbrainmusicplayer.services.cava

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CavaServiceTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        CavaService.stop()
    }

    override fun tearDown() {
        CavaService.stop()
        super.tearDown()
    }

    fun testReadBarsReturnsEmptyListInitially() {
        val bars = CavaService.readBars()
        assertTrue(bars.isEmpty())
    }

    fun testStopClearsState() {
        CavaService.stop()
        val bars = CavaService.readBars()
        assertTrue(bars.isEmpty())
    }

    fun testStartAndReadBars() {
        CavaService.start()
        val bars = CavaService.readBars()
        assertNotNull(bars)
    }
    fun testMultipleStarts() {
        CavaService.start()
        val firstBars = CavaService.readBars()
        CavaService.start()
        val secondBars = CavaService.readBars()
        assertEquals(firstBars, secondBars)
    }
}