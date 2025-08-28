package com.github.emiilia.meowsicplayer.toolWindow

import com.github.emiilia.meowsicplayer.services.cava.CavaService
import com.github.emiilia.meowsicplayer.services.playerctl.CrossPlatformPlayerService
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
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.KeyStroke

class MusicToolWindowFactory: ToolWindowFactory, DumbAware {
    private lateinit var playIcon: Icon
    private lateinit var pauseIcon: Icon
    private lateinit var musicIcon: Icon
    private lateinit var nextIcon: Icon
    private lateinit var prevIcon: Icon
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        CavaService.start()
        loadIcons()
        
        val panel = createMainPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "Meowsic Player", false)
        toolWindow.contentManager.addContent(content)
    }
    
    private fun loadIcons() {
        playIcon = loadIcon("/assets/icons/play.svg")
        pauseIcon = loadIcon("/assets/icons/pause.svg")
        musicIcon = loadIcon("/assets/icons/music.svg")
        nextIcon = loadIcon("/assets/icons/next.svg")
        prevIcon = loadIcon("/assets/icons/prev.svg")
    }
    
    private fun loadIcon(path: String): Icon {
        return IconLoader.getIcon(path, MusicToolWindowFactory::class.java)
    }
    
    private fun createMainPanel(project: Project): JPanel {
        val panel = JPanel(BorderLayout())
        
        val nowPlayingLabel = createNowPlayingLabel()
        val controlButtons = createControlButtons()
        val visualizerPanel = VisualizerPanel()
        
        panel.add(nowPlayingLabel, BorderLayout.NORTH)
        panel.add(controlButtons.panel, BorderLayout.SOUTH)
        panel.add(visualizerPanel, BorderLayout.CENTER)
        
        setupKeyboardShortcuts(panel, controlButtons)
        setupTimers(project, nowPlayingLabel, controlButtons.playPauseButton, visualizerPanel)
        
        return panel
    }
    
    private fun createNowPlayingLabel(): JLabel {
        return JLabel(CrossPlatformPlayerService.getNowPlaying()).apply {
            font = JBUI.Fonts.label().deriveFont(20f).asBold()
            horizontalAlignment = JLabel.CENTER
            verticalAlignment = JLabel.CENTER
            border = JBUI.Borders.empty(10)
            size = Dimension(300, 50)
            name = "NowPlayingLabel"
        }
    }
    
    private data class ControlButtons(
        val panel: JPanel,
        val playPauseButton: JButton,
        val nextButton: JButton,
        val prevButton: JButton
    )
    
    private fun createControlButtons(): ControlButtons {
        val playPauseButton = createPlayPauseButton()
        val nextButton = createNextButton()
        val prevButton = createPreviousButton()
        
        val controlsPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER, 5, 5)
            add(prevButton)
            add(playPauseButton)
            add(nextButton)
        }
        
        return ControlButtons(controlsPanel, playPauseButton, nextButton, prevButton)
    }
    
    private fun createPlayPauseButton(): JButton {
        return JButton().apply {
            icon = getIconForStatus(CrossPlatformPlayerService.getStatus())
            size = Dimension(50, 50)
            border = JBUI.Borders.empty(10)
            background = JBColor.PINK
            name = "PlayPauseButton"
            addActionListener { CrossPlatformPlayerService.playPause() }
        }
    }
    
    private fun createNextButton(): JButton {
        return JButton().apply {
            icon = nextIcon
            size = Dimension(40, 40)
            border = JBUI.Borders.empty(10)
            name = "NextButton"
            addActionListener { CrossPlatformPlayerService.next() }
        }
    }
    
    private fun createPreviousButton(): JButton {
        return JButton().apply {
            icon = prevIcon
            size = Dimension(40, 40)
            border = JBUI.Borders.empty(10)
            name = "PreviousButton"
            addActionListener { CrossPlatformPlayerService.previous() }
        }
    }
    
    private fun getIconForStatus(status: String): Icon {
        return when (status) {
            "Playing" -> pauseIcon
            "Paused" -> playIcon
            else -> musicIcon
        }
    }
    
    private fun setupTimers(
        project: Project,
        nowPlayingLabel: JLabel,
        playPauseButton: JButton,
        visualizerPanel: VisualizerPanel
    ) {
        val disposable = Disposer.newDisposable("MusicPlayerToolWindow")
        
        val playerUpdateTimer = createPlayerUpdateTimer(nowPlayingLabel, playPauseButton)
        val visualizerUpdateTimer = createVisualizerUpdateTimer(visualizerPanel)
        
        Disposer.register(disposable) {
            playerUpdateTimer.stop()
            visualizerUpdateTimer.stop()
        }
        
        Disposer.register(project, disposable)
        
        playerUpdateTimer.start()
        visualizerUpdateTimer.start()
    }
    
    private fun createPlayerUpdateTimer(nowPlayingLabel: JLabel, playPauseButton: JButton): Timer {
        return Timer(1000) {
            try {
                val title = CrossPlatformPlayerService.getNowPlaying()
                val status = CrossPlatformPlayerService.getStatus()
                
                nowPlayingLabel.text = "Now Playing: $title"
                playPauseButton.icon = getIconForStatus(status)
            } catch (e: Exception) {
                nowPlayingLabel.text = "Meowsic Player"
                playPauseButton.icon = musicIcon
            }
        }
    }
    
    private fun createVisualizerUpdateTimer(visualizerPanel: VisualizerPanel): Timer {
        return Timer(33) {
            try {
                val bars = CavaService.readBars()
                visualizerPanel.updateBars(bars)
            } catch (e: Exception) {
                println("Error updating visualizer: ${e.message}")
            }
        }
    }
    
    private fun setupKeyboardShortcuts(panel: JPanel, controlButtons: ControlButtons) {
        val inputMap = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        val actionMap = panel.actionMap
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_SPACE, "playPause") {
            controlButtons.playPauseButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_RIGHT, "nextTrack") {
            controlButtons.nextButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_LEFT, "prevTrack") {
            controlButtons.prevButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_N, "nextTrackN") {
            controlButtons.nextButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_P, "prevTrackP") {
            controlButtons.prevButton.doClick()
        }
        
        panel.isFocusable = true
        panel.requestFocusInWindow()
    }
    
    private fun addKeyboardAction(
        inputMap: javax.swing.InputMap,
        actionMap: javax.swing.ActionMap,
        keyCode: Int,
        actionName: String,
        action: () -> Unit
    ) {
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), actionName)
        actionMap.put(actionName, object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                action()
            }
        })
    }
}