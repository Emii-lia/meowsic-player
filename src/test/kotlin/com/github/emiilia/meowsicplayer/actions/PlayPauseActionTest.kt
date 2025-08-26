package com.github.emiilia.meowsicplayer.actions

import org.junit.Test
import org.junit.Assert.*

class PlayPauseActionTest {
    
    @Test
    fun testActionIsDumbAware() {
        val action = PlayPauseAction()
        assertTrue("PlayPauseAction should implement DumbAware", 
            action is com.intellij.openapi.project.DumbAware)
    }
    
    @Test
    fun testActionCanBeInstantiated() {
        val action = PlayPauseAction()
        assertNotNull("PlayPauseAction should be instantiable", action)
    }
    
    @Test
    fun testActionHasCorrectClass() {
        val action = PlayPauseAction()
        assertEquals("Should be PlayPauseAction class", 
            "PlayPauseAction", action.javaClass.simpleName)
    }
}