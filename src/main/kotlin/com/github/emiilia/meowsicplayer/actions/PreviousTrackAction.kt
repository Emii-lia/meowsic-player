package com.github.emiilia.meowsicplayer.actions

import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class PreviousTrackAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        PlayerctlService.previous()
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.text = "Previous Track"
        e.presentation.description = "Skip to previous track"
    }
}