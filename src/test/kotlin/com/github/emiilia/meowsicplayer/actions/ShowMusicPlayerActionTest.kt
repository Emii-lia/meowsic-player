package com.github.emiilia.meowsicplayer.actions

import org.junit.Test
import org.junit.Assert.*

class ShowMusicPlayerActionTest {
    
    @Test
    fun testActionIsDumbAware() {
        val action = ShowMusicPlayerAction()
        assertTrue("ShowMusicPlayerAction should implement DumbAware", 
            action is com.intellij.openapi.project.DumbAware)
    }
    
    @Test
    fun testActionCanBeInstantiated() {
        val action = ShowMusicPlayerAction()
        assertNotNull("ShowMusicPlayerAction should be instantiable", action)
    }
    
    @Test
    fun testActionHasCorrectClass() {
        val action = ShowMusicPlayerAction()
        assertEquals("Should be ShowMusicPlayerAction class", 
            "ShowMusicPlayerAction", action.javaClass.simpleName)
    }
}