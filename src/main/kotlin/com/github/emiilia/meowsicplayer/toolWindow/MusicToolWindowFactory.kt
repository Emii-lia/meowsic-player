package com.github.emiilia.meowsicplayer.toolWindow

import com.github.emiilia.meowsicplayer.services.cava.CavaService
import com.github.emiilia.meowsicplayer.services.playerctl.PlayerctlService
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBColor
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import javax.swing.Timer
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.ActionMap
import javax.swing.Icon
import javax.swing.InputMap
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.KeyStroke

class MusicToolWindowFactory: ToolWindowFactory, DumbAware {
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

        setupKeyboardShortcuts(panel, playPauseButton, nextButton, prevButton)
        
        val content = ContentFactory.getInstance().createContent(panel, "MusicPlayer", false)
        toolWindow.contentManager.addContent(content)

        val disposable = Disposer.newDisposable("MusicPlayerToolWindow")
        
        val playerUpdateTimer = Timer(1000) {
            try {
                val title = PlayerctlService.getNowPlaying()
                val status = PlayerctlService.getStatus()
                
                nowPlayingLabel.text = "Now Playing: $title"
                playPauseButton.icon = when (status) {
                    "Playing" -> pauseIcon
                    "Paused" -> playIcon
                    else -> musicIcon
                }
            } catch (e: Exception) {
                nowPlayingLabel.text = "Music Player"
                playPauseButton.icon = musicIcon
            }
        }
        
        val visualizerUpdateTimer = Timer(33) {
            try {
                val bars = CavaService.readBars()
                visualizerPanel.updateBars(bars)
            } catch (e: Exception) {
            }
        }
        
        Disposer.register(disposable) {
            playerUpdateTimer.stop()
            visualizerUpdateTimer.stop()
        }
        
        Disposer.register(project, disposable)
        
        playerUpdateTimer.start()
        visualizerUpdateTimer.start()
    }
    
    private fun setupKeyboardShortcuts(panel: JPanel, playPauseButton: JButton, nextButton: JButton, prevButton: JButton) {
        val inputMap = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        val actionMap = panel.actionMap
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "playPause")
        actionMap.put("playPause", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                playPauseButton.doClick()
            }
        })
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "nextTrack")
        actionMap.put("nextTrack", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                nextButton.doClick()
            }
        })
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prevTrack")
        actionMap.put("prevTrack", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                prevButton.doClick()
            }
        })
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0), "nextTrack")
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "prevTrack")
        
        panel.isFocusable = true
        panel.requestFocusInWindow()
    }
}
