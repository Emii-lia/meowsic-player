package com.github.emiilia.meowsicplayer.actions

import org.junit.Test
import org.junit.Assert.*

class PreviousTrackActionTest {
    
    @Test
    fun testActionIsDumbAware() {
        val action = PreviousTrackAction()
        assertTrue("PreviousTrackAction should implement DumbAware", 
            action is com.intellij.openapi.project.DumbAware)
    }
    
    @Test
    fun testActionCanBeInstantiated() {
        val action = PreviousTrackAction()
        assertNotNull("PreviousTrackAction should be instantiable", action)
    }
    
    @Test
    fun testActionHasCorrectClass() {
        val action = PreviousTrackAction()
        assertEquals("Should be PreviousTrackAction class", 
            "PreviousTrackAction", action.javaClass.simpleName)
    }
}