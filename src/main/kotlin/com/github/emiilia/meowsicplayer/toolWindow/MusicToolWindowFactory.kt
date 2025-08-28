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
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import javax.swing.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

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
        val mainPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            background = UIUtil.getPanelBackground()
            border = JBUI.Borders.empty(15, 20, 15, 20)
        }
        
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
            background = UIUtil.getPanelBackground()
            border = null
            dividerSize = 1
            resizeWeight = 0.5
        }
        
        val playerInfoCard = createPlayerInfoCard()
        val visualizerPanel = createVisualizerPanel()
        
        splitPane.leftComponent = playerInfoCard.panel
        splitPane.rightComponent = visualizerPanel
        
        mainPanel.add(splitPane, BorderLayout.CENTER)
        
        setupTimers(project, playerInfoCard, visualizerPanel)
        setupKeyboardShortcuts(mainPanel, playerInfoCard)
        
        return mainPanel
    }
    
    private data class PlayerInfoCard(
        val panel: JPanel,
        val albumArtLabel: JLabel,
        val trackLabel: JLabel,
        val artistLabel: JLabel,
        val playPauseButton: JButton,
        val nextButton: JButton,
        val prevButton: JButton
    )
    
    private fun createPlayerInfoCard(): PlayerInfoCard {
        val cardPanel = JBPanel<JBPanel<*>>(GridBagLayout()).apply {
            background = UIUtil.getPanelBackground()
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.border(), 1),
                JBUI.Borders.empty(20, 15, 20, 15)
            )
        }
        
        val albumArtLabel = JLabel().apply {
            preferredSize = Dimension(120, 120)
            minimumSize = Dimension(120, 120)
            maximumSize = Dimension(120, 120)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.border(), 1),
                JBUI.Borders.empty(2)
            )
            horizontalAlignment = JLabel.CENTER
            verticalAlignment = JLabel.CENTER
            icon = musicIcon
            background = JBColor(Color(0x2B2D30), Color(0x2B2D30))
            isOpaque = true
        }
        
        val trackLabel = JLabel("Meowsic Player").apply {
            font = JBUI.Fonts.label().deriveFont(16f).asBold()
            foreground = UIUtil.getLabelForeground()
            horizontalAlignment = JLabel.CENTER
        }
        
        val artistLabel = JLabel("No artist").apply {
            font = JBUI.Fonts.label().deriveFont(12f)
            foreground = UIUtil.getContextHelpForeground()
            horizontalAlignment = JLabel.CENTER
        }
        
        val prevButton = createStyledButton(prevIcon, 32, 32, "Previous Track")
        val playPauseButton = createStyledButton(
            getIconForStatus(CrossPlatformPlayerService.getStatus()), 
            40, 40, "Play/Pause"
        ).apply {
            background = JBColor(Color(0x5E8F5A), Color(0x5E8F5A))
            putClientProperty("PlayPauseButton", true)
        }
        val nextButton = createStyledButton(nextIcon, 32, 32, "Next Track")
        
        playPauseButton.addActionListener { CrossPlatformPlayerService.playPause() }
        nextButton.addActionListener { CrossPlatformPlayerService.next() }
        prevButton.addActionListener { CrossPlatformPlayerService.previous() }
        
        val controlsPanel = JBPanel<JBPanel<*>>(FlowLayout(FlowLayout.CENTER, 10, 0)).apply {
            background = UIUtil.getPanelBackground()
            add(prevButton)
            add(playPauseButton)
            add(nextButton)
        }
        
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            anchor = GridBagConstraints.CENTER
            fill = GridBagConstraints.NONE
            insets = JBUI.insets(0, 0, 15, 0)
        }
        
        cardPanel.add(albumArtLabel, gbc)
        
        gbc.gridy = 1
        gbc.insets = JBUI.insets(0, 0, 8, 0)
        cardPanel.add(trackLabel, gbc)
        
        gbc.gridy = 2
        gbc.insets = JBUI.insets(0, 0, 20, 0)
        cardPanel.add(artistLabel, gbc)
        
        gbc.gridy = 3
        gbc.insets = JBUI.insets(0)
        cardPanel.add(controlsPanel, gbc)
        
        return PlayerInfoCard(cardPanel, albumArtLabel, trackLabel, artistLabel, playPauseButton, nextButton, prevButton)
    }
    
    private fun createVisualizerPanel(): VisualizerPanel {
        return VisualizerPanel().apply {
            preferredSize = Dimension(-1, 120)
            minimumSize = Dimension(200, 120)
            background = UIUtil.getPanelBackground()
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.border(), 1),
                JBUI.Borders.empty(10)
            )
        }
    }
    
    private fun createStyledButton(icon: Icon, width: Int, height: Int, tooltip: String): JButton {
        return JButton(icon).apply {
            preferredSize = Dimension(width, height)
            minimumSize = Dimension(width, height)
            maximumSize = Dimension(width, height)
            toolTipText = tooltip
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            isFocusPainted = false
            isContentAreaFilled = false
            border = BorderFactory.createEmptyBorder()
            background = JBColor(Color(0x4C5052), Color(0x4C5052))
            
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    isContentAreaFilled = true
                    background = if (getClientProperty("PlayPauseButton") == true) {
                        JBColor(Color(0x6FA16C), Color(0x6FA16C))
                    } else {
                        JBColor(Color(0x5C6164), Color(0x5C6164))
                    }
                }
                
                override fun mouseExited(e: MouseEvent) {
                    background = if (getClientProperty("PlayPauseButton") == true) {
                        JBColor(Color(0x5E8F5A), Color(0x5E8F5A))
                    } else {
                        JBColor(Color(0x4C5052), Color(0x4C5052))
                    }
                }
                
                override fun mousePressed(e: MouseEvent) {
                    background = if (getClientProperty("PlayPauseButton") == true) {
                        JBColor(Color(0x4D7D4A), Color(0x4D7D4A))
                    } else {
                        JBColor(Color(0x3C4143), Color(0x3C4143))
                    }
                }
            })
        }
    }
    
    private fun getIconForStatus(status: String): Icon {
        return when (status) {
            "Playing" -> pauseIcon
            "Paused" -> playIcon
            else -> playIcon
        }
    }
    
    private fun setupTimers(
        project: Project,
        playerInfoCard: PlayerInfoCard,
        visualizerPanel: VisualizerPanel
    ) {
        val disposable = Disposer.newDisposable("MusicPlayerToolWindow")
        
        val playerUpdateTimer = Timer(1000) {
            try {
                val nowPlaying = CrossPlatformPlayerService.getNowPlaying()
                val status = CrossPlatformPlayerService.getStatus()
                
                val parts = nowPlaying.split(" - ", limit = 2)
                if (parts.size == 2) {
                    playerInfoCard.artistLabel.text = parts[0]
                    playerInfoCard.trackLabel.text = parts[1]
                } else {
                    playerInfoCard.trackLabel.text = nowPlaying
                    playerInfoCard.artistLabel.text = "Unknown Artist"
                }
                
                playerInfoCard.playPauseButton.icon = getIconForStatus(status)
            } catch (e: Exception) {
                playerInfoCard.trackLabel.text = "Meowsic Player"
                playerInfoCard.artistLabel.text = "No track playing"
                playerInfoCard.playPauseButton.icon = playIcon
            }
        }
        
        val visualizerUpdateTimer = Timer(33) {
            try {
                val bars = CavaService.readBars()
                visualizerPanel.updateBars(bars)
            } catch (e: Exception) {
                println("Error updating visualizer: ${e.message}")
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
    
    private fun setupKeyboardShortcuts(panel: JPanel, playerInfoCard: PlayerInfoCard) {
        val inputMap = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        val actionMap = panel.actionMap
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_SPACE, "playPause") {
            playerInfoCard.playPauseButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_RIGHT, "nextTrack") {
            playerInfoCard.nextButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_LEFT, "prevTrack") {
            playerInfoCard.prevButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_N, "nextTrackN") {
            playerInfoCard.nextButton.doClick()
        }
        
        addKeyboardAction(inputMap, actionMap, KeyEvent.VK_P, "prevTrackP") {
            playerInfoCard.prevButton.doClick()
        }
        
        panel.isFocusable = true
        panel.requestFocusInWindow()
    }
    
    private fun addKeyboardAction(
        inputMap: InputMap,
        actionMap: ActionMap,
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