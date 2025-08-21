package com.github.emiilia.jetbrainmusicplayer.utils

import com.intellij.util.ui.JBUI
import java.awt.Component
import java.awt.Graphics
import java.awt.Insets
import javax.swing.border.Border

class RoundedBorder: Border {
    private val radius: Int

    constructor(radius: Int) {
        this.radius = radius
    }

    override fun getBorderInsets(c: Component) = JBUI.insets(radius+1, radius+1, radius+2, radius)

    override fun isBorderOpaque() = false

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        g.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }
}
