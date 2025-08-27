package com.github.emiilia.meowsicplayer.actions

import com.github.emiilia.meowsicplayer.services.playerctl.CrossPlatformPlayerService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class PreviousTrackAction : AnAction(), DumbAware {
    
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    
    override fun actionPerformed(e: AnActionEvent) {
        CrossPlatformPlayerService.previous()
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.text = "Previous Track"
        e.presentation.description = "Skip to previous track"
    }
}