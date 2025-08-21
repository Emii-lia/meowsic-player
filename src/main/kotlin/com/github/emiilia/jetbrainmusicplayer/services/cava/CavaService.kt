package com.github.emiilia.jetbrainmusicplayer.services.cava

import com.jetbrains.rd.util.CopyOnWriteArrayList
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

object CavaService : CavaServiceInterface {
    private var cavaProcess: Process? = null
    private var readerThread: Thread? = null
    private val bars = CopyOnWriteArrayList<Int>()
    private val configPath = "${System.getProperty("user.home")}/.config/cava/config"

    override fun start() {
        if (cavaProcess != null && cavaProcess!!.isAlive) return
        try {
            cavaProcess = ProcessBuilder("cava", "-p", configPath)
                .redirectErrorStream(true)
                .start()
            val reader = BufferedReader(InputStreamReader(cavaProcess!!.inputStream))

            readerThread = thread(start = true, isDaemon = true) {
                while (cavaProcess?.isAlive == true) {
                    val line = reader.readLine() ?: continue
                    val newBars = line.split(";").mapNotNull { it.toIntOrNull() }
                    if (newBars.isNotEmpty()) {
                        bars.clear()
                        bars.addAll(newBars)
                    }
                }
            }

            println("Started Cava process with PID: ${cavaProcess!!.pid()}")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to start Cava process: ${e.message}")
        }
    }

    override fun stop() {
        try {
            cavaProcess?.destroy()
            cavaProcess = null
            readerThread?.interrupt()
            readerThread = null
            bars.clear()
            println("Cava process stopped.")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Failed to stop Cava process: ${e.message}")
        }
    }

    override fun readBars(): List<Int> {
        return if (bars.isNotEmpty()) {
            bars.toList()
        } else {
            emptyList()
        }
    }
}