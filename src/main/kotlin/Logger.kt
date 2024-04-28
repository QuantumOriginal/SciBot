package ind.glowingstone

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Logger {
    companion object {
        private val logBuffer: MutableList<String> = mutableListOf()
        private val lock = Any()
        fun log(message: String?, level: LogLevel? = LogLevel.UNKNOWN) {
            if (level == null) {
                println("Log level cannot be null")
                return
            }

            val now = Date()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timestamp = sdf.format(now)

            val logEntry = String.format("[%s] [%s] %s", timestamp, level, message)

            synchronized(lock) {
                logBuffer.add(logEntry)
            }
        }

        fun startLogWriter(logFilePath: String?, intervalMillis: Long) {
            if (logFilePath.isNullOrEmpty()) {
                throw IllegalArgumentException("Log file path cannot be null or empty")
            }

            val timer = Timer(true)
            timer.scheduleAtFixedRate(LogWriterTask(logFilePath), intervalMillis, intervalMillis)
        }

        enum class LogLevel {
            INFO, WARNING, ERROR, UNKNOWN
        }

        private class LogWriterTask(private val logFilePath: String) : TimerTask() {
            override fun run() {
                val logsToWrite: List<String>

                synchronized(lock) {
                    logsToWrite = logBuffer.toList()
                    logBuffer.clear()
                }

                if (logsToWrite.isNotEmpty()) {
                    try {
                        BufferedWriter(FileWriter(logFilePath, true)).use { writer ->
                            logsToWrite.forEach { log ->
                                writer.write("$log\n")
                            }
                        }
                    } catch (e: IOException) {
                        println("Error writing logs: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
