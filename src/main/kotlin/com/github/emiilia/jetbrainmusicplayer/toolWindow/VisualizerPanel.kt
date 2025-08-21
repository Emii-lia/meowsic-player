package com.github.emiilia.jetbrainmusicplayer.toolWindow

import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics
import javax.swing.JPanel

class VisualizerPanel: JPanel() {
    private var bars: List<Int> = emptyList()
    private var displayedBars: MutableList<Double> = mutableListOf()

    private val riseSpeed = 0.5
    private val fallSpeed = 0.1

    fun updateBars(newBars: List<Int>) {
        if (displayedBars.isEmpty()) {
            displayedBars = newBars.map { it.toDouble() }.toMutableList()
        } else {
            for (i in displayedBars.indices) {
                val target = newBars[i].toDouble()
                val current = displayedBars[i]

                displayedBars[i] = if (current < target) {
                    current + (target - current) * riseSpeed
                } else {
                    current - (current - target) * fallSpeed
                }
            }
        }
        bars = displayedBars.map { it.toInt() }
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