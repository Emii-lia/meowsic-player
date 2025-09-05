package com.github.emiilia.meowsicplayer.services.cava

import com.github.emiilia.meowsicplayer.utils.Platform
import com.intellij.openapi.diagnostic.Logger
import com.jetbrains.rd.util.CopyOnWriteArrayList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File
import kotlin.concurrent.thread

object CavaService : CavaServiceInterface {
    private val logger = Logger.getInstance(CavaService::class.java)
    private val processLock = Any()
    @Volatile private var cavaProcess: Process? = null
    @Volatile private var readerThread: Thread? = null
    private val bars = CopyOnWriteArrayList<Int>()
    @Volatile private var tempConfigPath: String? = null
    @Volatile private var cavaAvailable: Boolean? = null

    private fun createTempConfig(): String {
        if (tempConfigPath != null && File(tempConfigPath!!).exists()) {
            return tempConfigPath!!
        }

        try {
            val configFileName = when (Platform.os) {
                Platform.OS.WINDOWS -> "/config/config-windows"
                Platform.OS.MAC -> "/config/config-macos"
                else -> "/config/config-linux"
            }
            
            val configResource = CavaService::class.java.getResourceAsStream(configFileName)
                ?: throw IllegalStateException("Bundled cava config not found in resources: $configFileName")
            
            val tempConfigFile = File.createTempFile("cava_config_", ".conf")
            tempConfigFile.deleteOnExit()
            
            val configContent = configResource.bufferedReader().use { it.readText() }
            tempConfigFile.writeText(configContent)
            
            tempConfigPath = tempConfigFile.absolutePath
            return tempConfigPath!!
        } catch (e: Exception) {
            throw RuntimeException("Failed to create temporary cava config: ${e.message}", e)
        }
    }

    private fun isCavaAvailable(): Boolean {
        if (cavaAvailable != null) {
            return cavaAvailable!!
        }
        
        return try {
            val command = when (Platform.os) {
                Platform.OS.WINDOWS -> "where"
                else -> "which"
            }
            
            val process = ProcessBuilder(command, "cava")
                .redirectErrorStream(false)
                .start()
            val exitCode = process.waitFor()
            cavaAvailable = (exitCode == 0)
            cavaAvailable!!
        } catch (e: Exception) {
            logger.debug("Error checking cava availability", e)
            cavaAvailable = false
            false
        }
    }
    
    override fun start() {
        if (!isCavaAvailable()) {
            logger.warn("Cava is not installed. Visualizer will be disabled.")
            return
        }
        
        synchronized(processLock) {
            val currentProcess = cavaProcess
            if (currentProcess != null && currentProcess.isAlive) return
        }
        
        synchronized(processLock) {
            try {
                val configPath = createTempConfig()
                
                cavaProcess = ProcessBuilder("cava", "-p", configPath)
                    .redirectErrorStream(false)
                    .start()
                
                val reader = BufferedReader(InputStreamReader(cavaProcess!!.inputStream))

                readerThread = thread(start = true, isDaemon = true) {
                    try {
                        var currentProcess: Process?
                        while (true) {
                            synchronized(processLock) {
                                currentProcess = cavaProcess
                            }
                            if (currentProcess?.isAlive != true) break
                            
                            try {
                                val line = reader.readLine() ?: break
                                if (line.isNotBlank()) {
                                    val newBars = line.split(";").mapNotNull { it.toIntOrNull() }
                                    if (newBars.isNotEmpty()) {
                                        synchronized(bars) {
                                            bars.clear()
                                            bars.addAll(newBars)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                logger.debug("Error reading cava output line", e)
                                Thread.sleep(50)
                            }
                        }
                    } finally {
                        try {
                            reader.close()
                        } catch (e: Exception) {
                            logger.debug("Error closing cava reader", e)
                        }
                    }
                }

                logger.info("Started Cava process with PID: ${cavaProcess!!.pid()}")
            } catch (e: Exception) {
                cavaProcess?.let { process ->
                    try {
                        process.destroyForcibly()
                    } catch (cleanupException: Exception) {
                        logger.debug("Error force-destroying cava process during startup cleanup", cleanupException)
                    }
                }
                cavaProcess = null
                readerThread?.interrupt()
                readerThread = null
                bars.clear()
                
                logger.error("Failed to start Cava process", e)
            }
        }
    }

    override fun stop() {
        synchronized(processLock) {
            try {
                readerThread?.interrupt()
                
                cavaProcess?.let { process ->
                process.destroy()
                
                val terminated = try {
                    process.waitFor(1, java.util.concurrent.TimeUnit.SECONDS)
                } catch (_: Exception) {
                    false
                }
                
                if (!terminated) {
                    process.destroyForcibly()
                    try {
                        process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)
                    } catch (e: Exception) {
                        logger.debug("Process didn't terminate after force kill, continuing cleanup", e)
                    }
                }
            }
            
            readerThread?.let { thread ->
                try {
                    thread.join(1000)
                } catch (e: InterruptedException) {
                    logger.debug("Reader thread join interrupted, continuing cleanup", e)
                }
            }
            
            cavaProcess = null
            readerThread = null
            bars.clear()
            
            tempConfigPath?.let { path ->
                try {
                    File(path).delete()
                } catch (e: Exception) {
                    logger.debug("Failed to delete temp config file: $path", e)
                }
                tempConfigPath = null
            }
            
                logger.info("Cava process stopped.")
            } catch (e: Exception) {
                logger.error("Failed to stop Cava process", e)
            }
        }
    }

    override fun readBars(): List<Int> {
        return synchronized(bars) {
            if (bars.isNotEmpty()) {
                bars.toList()
            } else {
                emptyList()
            }
        }
    }
}