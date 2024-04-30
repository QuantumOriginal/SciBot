import ind.glowingstone.Configurations
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.Collections
import java.util.logging.Level

class Logger(private val prefix: String) : Runnable {
    private val RESET = "\u001B[0m"
    private val RED = "\u001B[31m"
    private val GREEN = "\u001B[32m"
    private val YELLOW = "\u001B[33m"
    private val BLUE = "\u001B[34m"
    fun log(msg: String, level: Level) {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val logTime = dateFormat.format(Date())

        val (color, levelName) = when (level) {
            Level.INFO -> GREEN to "INFO"
            Level.WARNING -> YELLOW to "WARNING"
            Level.SEVERE -> RED to "SEVERE"
            else -> BLUE to "UNKNOWN"
        }

        val coloredLogEntry = "$color[$levelName][$logTime][$prefix] $msg$RESET"
        val plainLogEntry = "[$levelName][$logTime][$prefix] $msg"

        println(coloredLogEntry)

        synchronized(LogList) {
            LogList.add(plainLogEntry)
        }
    }

    override fun run() {
        val logPath = Paths.get(LogDir).toAbsolutePath()

        if (Files.notExists(logPath) || !Files.isDirectory(logPath)) {
            try {
                Files.createDirectories(logPath)
            } catch (e: IOException) {
                println("Failed to create log directory: ${e.message}")
                return
            }
        }

        var logFile = getCurrentLogFile(logPath)

        if (logFile.length() > MAX_LOG_SIZE_MB * 1024 * 1024) {
            logFile = createNewLogFile(logPath)
        }

        try {
            BufferedWriter(FileWriter(logFile, true)).use { writer ->
                synchronized(LogList) {
                    LogList.forEach { log ->
                        writer.write(log)
                        writer.newLine()
                    }
                    LogList.clear()
                }
            }
        } catch (e: IOException) {
            println("Failed to write logs: ${e.message}")
        }
    }

    companion object {
        const val MAX_LOG_SIZE_MB = 100
        val LogList: MutableList<String> = Collections.synchronizedList(ArrayList())
        val LogDir: String = Configurations().get("log-dir").toString()

        private fun getCurrentLogFile(logPath: java.nio.file.Path): File {
            val logFiles = logPath.toFile().listFiles()?.sortedBy { it.name } ?: listOf()
            return logFiles.lastOrNull() ?: createNewLogFile(logPath)
        }

        private fun createNewLogFile(logPath: java.nio.file.Path): File {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
            val logFileName = "log_" + dateFormat.format(Date()) + ".txt"
            return logPath.resolve(logFileName).toFile()
        }
    }
}
