package com.github.emiilia.meowsicplayer.toolWindow

import com.github.emiilia.meowsicplayer.services.cava.CavaService
import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

class MusicToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        CavaService.start()

        val panel = JPanel(BorderLayout())

        val nowPlayingLabel = JLabel(PlayerctlService.getNowPlaying())
        nowPlayingLabel.apply {
            font = JBUI.Fonts.label().deriveFont(16f)
            horizontalAlignment = JLabel.CENTER
            verticalAlignment = JLabel.CENTER
            border = JBUI.Borders.empty(10)
            size = Dimension(300, 50)
            font = JBUI.Fonts.label().deriveFont(20f).asBold()
            name = "NowPlayingLabel"
        }
        panel.add(nowPlayingLabel, BorderLayout.NORTH)

        val controls = JPanel()
        val playPauseButton = JButton()
        val nextButton = JButton()
        val prevButton = JButton()

        // Icons
        fun loadIcon(path: String): Icon {
            return IconLoader.getIcon(path, MusicToolWindowFactory::class.java)
        }

        val playIcon = loadIcon("/assets/icons/play.svg")
        val pauseIcon = loadIcon("/assets/icons/pause.svg")
        val musicIcon = loadIcon("/assets/icons/music.svg")
        val nextIcon = loadIcon("/assets/icons/next.svg")
        val prevIcon = loadIcon("/assets/icons/prev.svg")

        playPauseButton.apply {
            icon = when (PlayerctlService.getStatus()) {
                "Playing" -> pauseIcon
                "Paused" -> playIcon
                else -> musicIcon
            }
            size = Dimension(50, 50)
            border = JBUI.Borders.empty(10)
            background = JBColor.PINK
            name = "PlayPauseButton"
        }
        playPauseButton.addActionListener { PlayerctlService.playPause() }

        nextButton.apply {
            icon = nextIcon
            size = Dimension(40, 40)
            border = JBUI.Borders.empty(10)
            name = "NextButton"
        }
        nextButton.addActionListener { PlayerctlService.next() }

        prevButton.apply {
            icon = prevIcon
            size = Dimension(40, 40)
            border = JBUI.Borders.empty(10)
            name = "PreviousButton"
        }
        prevButton.addActionListener { PlayerctlService.previous() }

        controls.add(prevButton)
        controls.add(playPauseButton)
        controls.add(nextButton)

        controls.apply {
            layout = FlowLayout(FlowLayout.CENTER, 5, 5)
        }
        panel.add(controls, BorderLayout.SOUTH)

        val visualizerPanel = VisualizerPanel()
        panel.add(visualizerPanel, BorderLayout.CENTER)

        val content = ContentFactory.getInstance().createContent(panel, "MusicPlayer", false)
        toolWindow.contentManager.addContent(content)

        Thread {
            while (true) {
                try {
                    val title = PlayerctlService.getNowPlaying()
                    SwingUtilities.invokeLater {
                        nowPlayingLabel.text = "Now Playing: $title"
                        playPauseButton.icon = when (PlayerctlService.getStatus()) {
                            "Playing" -> pauseIcon
                            "Paused" -> playIcon
                            else -> musicIcon
                        }
                    }

                    val bars = CavaService.readBars()
                    SwingUtilities.invokeLater {
                        visualizerPanel.updateBars(bars)
                    }
                    Thread.sleep(100 / 30)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.apply {
            isDaemon = true
            start()
        }

    }
}
