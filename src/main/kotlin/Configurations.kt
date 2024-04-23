package ind.glowingstone

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileNotFoundException

class Configurations {
    var configMap: HashMap<String, Any> = hashMapOf()
    val defaultConfigMap: HashMap<String, Any> = hashMapOf()

    fun readConfig() {
        val yaml = Yaml()
        val file = File("config.yml")
        try {
            configMap = yaml.load(file.inputStream()) as HashMap<String, Any>
        } catch (e: FileNotFoundException) {
            println("Error: Configuration file not found.")
        } catch (e: ClassCastException) {
            println("Error: Configuration data type mismatch.")
        }
    }
}
