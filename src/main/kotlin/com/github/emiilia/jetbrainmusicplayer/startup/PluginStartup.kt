package com.github.emiilia.jetbrainmusicplayer.startup

import com.github.emiilia.jetbrainmusicplayer.services.cava.CavaService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class PluginStartup: ProjectManagerListener {
    override fun projectClosed(project: Project) {
        super.projectClosed(project)
        CavaService.stop()
    }
}