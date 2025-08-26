package com.github.emiilia.meowsicplayer.actions

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class NextTrackAction : AnAction(), DumbAware {
    
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    
    override fun actionPerformed(e: AnActionEvent) {
        PlayerctlService.next()
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.text = "Next Track"
        e.presentation.description = "Skip to next track"
    }
}