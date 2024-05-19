package ind.glowingstone

import Logger
import org.scibot.Interfaces.*
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.HashMap
import java.util.logging.Level

class Configurations {
    val logger: Logger = Logger("CONFIG")
    var configMap: HashMap<String, Any> = hashMapOf()
    val defaultConfigMap: HashMap<String, Any> = hashMapOf(
        "configuration_version" to Host.HOST_VERSION,
        "server_port" to 9912,
        "upload_url" to "localhost:8080",
        "log-dir" to "./logs",
        "debug" to false,
        "auth" to "YOUR_KEY_HERE"
    )

    init {
        val yaml = Yaml()
        val file = File("config.yml")

        try {
            if (!file.exists()) {
                logger.log("Configuration file not found. Creating new default config.", Level.WARNING)
                createDefaultConfig(yaml, file)
            } else {
                configMap = yaml.load(file.inputStream()) as HashMap<String, Any>
                checkAndUpdateConfig(yaml, file)
            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
            logger.log("Error: Configuration data type mismatch.", Level.SEVERE)
        } catch (e: IOException) {
            logger.log("Error: Failed to read or write configuration file.", Level.SEVERE)
        }
    }
    private fun createDefaultConfig(yaml: Yaml, file: File) {
        try {
            val fileWriter = FileWriter(file)
            yaml.dump(defaultConfigMap, fileWriter)
            configMap = HashMap(defaultConfigMap)
        } catch (e: IOException) {
            logger.log("Error: Could not create the default configuration.", Level.SEVERE)
        }
    }
    private fun checkAndUpdateConfig(yaml: Yaml, file: File) {
        var isUpdated = false
        val currentVersion = configMap["configuration_version"] as Double
        val defaultVersion:Double = Host.HOST_VERSION
        if (currentVersion < defaultVersion) {
            configMap["configuration_version"] = defaultVersion
            isUpdated = true
        }
        for ((key, value) in defaultConfigMap) {
            if (!configMap.containsKey(key)) {
                configMap[key] = value
                isUpdated = true
            }
        }
        if (isUpdated) {
            try {
                val fileWriter = FileWriter(file)
                yaml.dump(configMap, fileWriter)
                logger.log("Configuration file has been updated.", Level.INFO)
            } catch (e: IOException) {
                logger.log("Error: Could not update the configuration file.", Level.SEVERE)
            }
        }
    }

    fun get(key: String): Any? {
        return configMap[key]
    }
}
