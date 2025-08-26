package com.github.emiilia.meowsicplayer.actions

import org.junit.Test
import org.junit.Assert.*

class NextTrackActionTest {
    
    @Test
    fun testActionIsDumbAware() {
        val action = NextTrackAction()
        assertTrue("NextTrackAction should implement DumbAware", 
            action is com.intellij.openapi.project.DumbAware)
    }
    
    @Test
    fun testActionCanBeInstantiated() {
        val action = NextTrackAction()
        assertNotNull("NextTrackAction should be instantiable", action)
    }
    
    @Test
    fun testActionHasCorrectClass() {
        val action = NextTrackAction()
        assertEquals("Should be NextTrackAction class", 
            "NextTrackAction", action.javaClass.simpleName)
    }
}