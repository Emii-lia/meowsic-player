package com.github.emiilia.meowsicplayer.actions

import com.github.emiilia.meowsicplayer.services.playerctl.CrossPlatformPlayerService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class PlayPauseAction : AnAction(), DumbAware {
    
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    
    override fun actionPerformed(e: AnActionEvent) {
        CrossPlatformPlayerService.playPause()
    }
    
    override fun update(e: AnActionEvent) {
        val status = try {
            CrossPlatformPlayerService.getStatus()
        } catch (ex: Exception) {
            "Unknown"
        }
        
        e.presentation.text = when (status) {
            "Playing" -> "Pause Music"
            "Paused" -> "Play Music"
            else -> "Play/Pause Music"
        }
    }
}