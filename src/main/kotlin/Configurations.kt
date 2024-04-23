package ind.glowingstone

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileWriter
import java.io.IOException

class Configurations {
    var configMap: HashMap<String, Any> = hashMapOf()
    val defaultConfigMap: HashMap<String, Any> = hashMapOf(
        "server_port" to 9912,
        "upload_url" to "localhost:8080"
    )

    init{
        val yaml = Yaml()
        val file = File("config.yml")

        try {
            if (!file.exists()) {
                println("Configuration file not found. Creating new default config.")
                val fileWriter = FileWriter(file)
                yaml.dump(defaultConfigMap, fileWriter)
                configMap = defaultConfigMap
            } else {
                configMap = yaml.load(file.inputStream()) as HashMap<String, Any>
            }
        } catch (e: ClassCastException) {
            println("Error: Configuration data type mismatch.")
        } catch (e: IOException) {
            println("Error: Failed to create configuration file.")
        }
    }
    fun get(key: String): Any? {
        /*if (!configMap.containsKey(key)) {
            return defaultConfigMap[key]
        }

         */
        return configMap[key]
    }
}
