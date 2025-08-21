package com.github.emiilia.jetbrainmusicplayer.toolWindow

import com.intellij.ui.JBColor
import java.awt.Graphics
import javax.swing.JPanel

class VisualizerPanel: JPanel() {
    private var bars: List<Int> = emptyList()

    fun updateBars(newBars: List<Int>) {
        bars = newBars
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (bars.isEmpty()) return

        val barWidth = width / bars.size
        for ((i, value) in bars.withIndex()) {
            val barHeight = (value / 100.0 * height).toInt()
            g.color = JBColor.PINK
            g.fillRect(i * barWidth, height - barHeight, barWidth - 2, barHeight)

        }
    }
}