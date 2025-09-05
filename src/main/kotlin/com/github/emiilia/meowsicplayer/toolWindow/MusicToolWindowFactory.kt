package com.github.emiilia.meowsicplayer.toolWindow

import com.github.emiilia.meowsicplayer.services.cava.CavaService
import com.github.emiilia.meowsicplayer.services.playerctl.CrossPlatformPlayerService
import com.intellij.openapi.diagnostic.Logger
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
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import java.util.concurrent.CompletableFuture

class MusicToolWindowFactory: ToolWindowFactory, DumbAware {
    private val logger = Logger.getInstance(MusicToolWindowFactory::class.java)
    private lateinit var playIcon: Icon
    private lateinit var pauseIcon: Icon
    private lateinit var musicIcon: Icon
    private lateinit var nextIcon: Icon
    private lateinit var prevIcon: Icon
    private val albumArtCache = mutableMapOf<String, ImageIcon>()
    private var currentAlbumArtUrl: String = ""
    
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
            border = JBUI.Borders.empty(15, 20)
        }
        
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
            background = UIUtil.getPanelBackground()
            border = null
            dividerSize = 0
            resizeWeight = 0.33
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
                JBUI.Borders.empty(20, 15)
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
            verticalAlignment = JLabel.CENTER
            border = JBUI.Borders.empty(5, 15)
            preferredSize = Dimension(-1, 40)
        }
        
        val artistLabel = JLabel("No artist").apply {
            font = JBUI.Fonts.label().deriveFont(12f)
            foreground = UIUtil.getContextHelpForeground()
            horizontalAlignment = JLabel.CENTER
            verticalAlignment = JLabel.CENTER
            border = JBUI.Borders.empty(0, 15, 5, 15)
            preferredSize = Dimension(-1, 25)
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
            insets = JBUI.insetsBottom(15)
            weightx = 0.0
        }
        
        cardPanel.add(albumArtLabel, gbc)
        
        gbc.gridy = 1
        gbc.insets = JBUI.insetsBottom(8)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        cardPanel.add(trackLabel, gbc)
        
        gbc.gridy = 2
        gbc.insets = JBUI.insetsBottom(20)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0
        cardPanel.add(artistLabel, gbc)
        
        gbc.gridy = 3
        gbc.insets = JBUI.emptyInsets()
        gbc.fill = GridBagConstraints.NONE
        gbc.weightx = 0.0
        cardPanel.add(controlsPanel, gbc)
        
        return PlayerInfoCard(cardPanel, albumArtLabel, trackLabel, artistLabel, playPauseButton, nextButton, prevButton)
    }
    
    private fun createVisualizerPanel(): VisualizerPanel {
        return VisualizerPanel().apply {
            preferredSize = Dimension(-1, 120)
            minimumSize = Dimension(200, 120)
            background = UIUtil.getPanelBackground().darker()
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
                        JBColor(JBColor.PINK.darker(), JBColor.PINK)
                    } else {
                        JBColor(Color(0x5C6164), Color(0x5C6164))
                    }
                }
                
                override fun mouseExited(e: MouseEvent) {
                    background = UIUtil.getPanelBackground()
                }
                
                override fun mousePressed(e: MouseEvent) {
                    background = if (getClientProperty("PlayPauseButton") == true) {
                        JBColor(JBColor.PINK.darker().darker(), JBColor.PINK.darker())
                    } else {
                        JBColor(Color(0x3C4143), Color(0x3C4143))
                    }
                }
            })
        }
    }
    
    private fun loadAlbumArt(albumArtUrl: String, albumArtLabel: JLabel) {
        if (albumArtUrl.isBlank() || !isValidAlbumArtUrl(albumArtUrl)) {
            SwingUtilities.invokeLater {
                albumArtLabel.icon = musicIcon
            }
            return
        }
        
        albumArtCache[albumArtUrl]?.let { cachedIcon ->
            SwingUtilities.invokeLater {
                albumArtLabel.icon = cachedIcon
            }
            return
        }
        
        CompletableFuture.supplyAsync {
            try {
                val uri = URI.create(albumArtUrl)
                val originalImage = ImageIO.read(uri.toURL())
                
                val scaledImage = BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB)
                val g2d = scaledImage.createGraphics()
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                g2d.drawImage(originalImage, 0, 0, 120, 120, null)
                g2d.dispose()
                
                val icon = ImageIcon(scaledImage)
                albumArtCache[albumArtUrl] = icon
                
                if (albumArtCache.size > 10) {
                    val oldestKey = albumArtCache.keys.first()
                    albumArtCache.remove(oldestKey)
                }
                
                icon
            } catch (e: Exception) {
                logger.warn("Failed to load album art from $albumArtUrl", e)
                musicIcon
            }
        }.thenAcceptAsync({ icon ->
            SwingUtilities.invokeLater {
                albumArtLabel.icon = icon
            }
        }, SwingUtilities::invokeLater)
    }
    
    private fun isValidAlbumArtUrl(url: String): Boolean {
        return try {
            URI.create(url)
            url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")
        } catch (_: Exception) {
            false
        }
    }
    
    private fun truncateText(text: String, maxLength: Int = 30): String {
        return if (text.length > maxLength) {
            text.take(maxLength - 3) + "..."
        } else {
            text
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
        
        var playerUpdateTimer: Timer? = null
        var visualizerUpdateTimer: Timer? = null
        
        try {
            playerUpdateTimer = Timer(1000) {
                try {
                    if (project.isDisposed) {
                        return@Timer
                    }
                    
                    val metadata = CrossPlatformPlayerService.getMetadata()
                    val status = CrossPlatformPlayerService.getStatus()
                    
                    SwingUtilities.invokeLater {
                        if (!project.isDisposed) {
                            playerInfoCard.trackLabel.text = truncateText(metadata.getDisplayTitle(), 60)
                            playerInfoCard.artistLabel.text = truncateText(metadata.getDisplayArtist(), 30)
                            playerInfoCard.playPauseButton.icon = getIconForStatus(status)
                            
                            if (metadata.albumArtUrl != currentAlbumArtUrl) {
                                currentAlbumArtUrl = metadata.albumArtUrl
                                loadAlbumArt(metadata.albumArtUrl, playerInfoCard.albumArtLabel)
                            }
                        }
                    }
                } catch (_: Exception) {
                    if (!project.isDisposed) {
                        SwingUtilities.invokeLater {
                            playerInfoCard.trackLabel.text = "Meowsic Player"
                            playerInfoCard.artistLabel.text = "No track playing"
                            playerInfoCard.playPauseButton.icon = playIcon
                            playerInfoCard.albumArtLabel.icon = musicIcon
                        }
                    }
                }
            }
            
            visualizerUpdateTimer = Timer(33) {
                try {
                    if (project.isDisposed) {
                        return@Timer
                    }
                    
                    val bars = CavaService.readBars()
                    SwingUtilities.invokeLater {
                        if (!project.isDisposed) {
                            visualizerPanel.updateBars(bars)
                        }
                    }
                } catch (e: Exception) {
                    if (!project.isDisposed) {
                        logger.debug("Error updating visualizer", e)
                    }
                }
            }
            
            Disposer.register(disposable) {
                try {
                    playerUpdateTimer?.let { timer ->
                        if (timer.isRunning) {
                            timer.stop()
                        }
                    }
                    visualizerUpdateTimer?.let { timer ->
                        if (timer.isRunning) {
                            timer.stop()
                        }
                    }
                } catch (e: Exception) {
                    logger.debug("Error during timer disposal", e)
                }
            }
            
            Disposer.register(project, disposable)
            
            playerUpdateTimer.start()
            visualizerUpdateTimer.start()
            
        } catch (e: Exception) {
            try {
                playerUpdateTimer?.stop()
                visualizerUpdateTimer?.stop()
                Disposer.dispose(disposable)
            } catch (_: Exception) {
            }
            throw e
        }
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