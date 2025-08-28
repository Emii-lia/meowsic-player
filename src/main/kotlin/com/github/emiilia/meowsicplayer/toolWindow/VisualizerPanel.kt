package com.github.emiilia.meowsicplayer.toolWindow

import com.intellij.ui.JBColor
import java.awt.Graphics
import javax.swing.JPanel

class VisualizerPanel: JPanel() {
    private var bars: List<Int> = emptyList()
    private var displayedBars: MutableList<Double> = mutableListOf()

    private val riseSpeed = 0.75
    private val fallSpeed = 0.3

    fun updateBars(newBars: List<Int>): List<Int> {
        if (displayedBars.isEmpty()) {
            displayedBars = newBars.map { it.toDouble() }.toMutableList()
        } else {
            if (displayedBars.size != newBars.size) {
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
        }
        bars = displayedBars.map { it.toInt() }
        repaint()
        return bars
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