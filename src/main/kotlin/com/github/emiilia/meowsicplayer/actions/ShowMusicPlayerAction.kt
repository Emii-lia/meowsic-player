package com.github.emiilia.meowsicplayer.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindowManager

class ShowMusicPlayerAction : AnAction(), DumbAware {
    
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Meowsic Player")
        
        toolWindow?.let {
            if (it.isVisible) {
                it.hide()
            } else {
                it.show()
                it.activate(null)
            }
        }
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabledAndVisible = project != null
        
        if (project != null) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Meowsic Player")
            e.presentation.text = if (toolWindow?.isVisible == true) {
                "Hide Music Player"
            } else {
                "Show Music Player"
            }
            e.presentation.description = "Toggle Music Player tool window visibility"
        }
    }
}