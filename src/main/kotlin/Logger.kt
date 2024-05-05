import Logger.Companion.LogDir
import Logger.Companion.LogList
import Logger.Companion.MAX_LOG_SIZE_MB
import ind.glowingstone.Configurations
import ind.glowingstone.Host
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
import java.util.logging.Level.INFO

class Logger(private val prefix: String? = "") : SimpleLogger {
    val config = Host.configInstance
    private val RESET = "\u001B[0m"
    private val RED = "\u001B[31m"
    private val GREEN = "\u001B[32m"
    private val YELLOW = "\u001B[33m"
    private val BLUE = "\u001B[34m"
    override fun log(msg: String, level: Level?) {
        val dateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss")
        val logTime = dateFormat.format(Date())
        var lvl = level
        if (lvl == null) lvl = INFO
        val (color, levelName) = when (lvl) {
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

    override fun debug(msg: String) {
        if (config.get("debug") as Boolean){
            val dateFormat = SimpleDateFormat("yyyyMMdd HH:mm:ss")
            val logTime = dateFormat.format(Date())

            val (color, levelName) = BLUE to "DEBUG"

            val coloredLogEntry = "$color[$levelName][$logTime][$prefix] $msg$RESET"
            val plainLogEntry = "[$levelName][$logTime][$prefix] $msg"

            println(coloredLogEntry)

            synchronized(LogList) {
                LogList.add(plainLogEntry)
            }
        }
    }

    fun getInstance(): Logger {
        return this
    }

    companion object {
        const val MAX_LOG_SIZE_MB = 100
        val LogList: MutableList<String> = Collections.synchronizedList(ArrayList())
        val LogDir: String = Configurations().get("log-dir").toString()
    }
}
class LogWriter : Runnable{
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
