import ind.glowingstone.Annonations
import ind.glowingstone.Host
import ind.glowingstone.MessageConstructor
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.net.URLClassLoader
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.logging.Level
import java.util.logging.Level.SEVERE
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod

interface Plugin {
    fun start(Logger: SimpleLogger, sender: SimpleSender)
}

class PluginManager(private val pluginDirectory: String) {
    val logger:Logger = Logger("PLUGIN_MANAGER")
    companion object{
        val loadedPlugins = mutableListOf<Plugin>()
    }
    private val classLoader: URLClassLoader

    init {
        val pluginDir = File(pluginDirectory)
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        val jarUrls = jarFiles?.map { it.toURI().toURL() }?.toTypedArray()
        classLoader = URLClassLoader(jarUrls)
    }
    fun loadPlugins() {
        val pluginDir = File(pluginDirectory)
        if(pluginDir.listFiles()?.size == 0) {
            return
        }
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        jarFiles?.forEach { jarFile ->
            try {
                val pluginClass = classLoader.loadClass(loadPluginConfig("main-class", jarFile.toString()).toString())
                if (Plugin::class.java.isAssignableFrom(pluginClass)) {
                    val pluginInstance = pluginClass.kotlin.createInstance() as Plugin
                    loadedPlugins.add(pluginInstance)
                    val logger: Logger = Logger(loadPluginConfig("plugin-name", jarFile.toString()).toString())
                    val sender: Sender = Sender()
                    pluginInstance.start(logger,sender)
                }
            } catch (e: Exception) {
                logger.log("无法加载插件 ${jarFile.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun invokePluginMethod(type: Annotype, event: Events.MajorEvent, args:MessageConstructor.Types) {
        loadedPlugins.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PlainHandler>() != null }

            plainHandlerMethods.forEach { method ->
                println("Method: ${method.name}, Parameters: ${method.parameters.map { it.type }}")
            }
        }

        loadedPlugins.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { (it.findAnnotation<Annonations.PlainHandler>() != null)}
                .map { it }

            val privateHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PrivateHandler>() != null}
                .map { it }
            plainHandlerMethods.forEach { method ->
                println(method.name)
            }
            when (type) {
                Annotype.PLAIN -> {
                    plainHandlerMethods.forEach { method ->
                        method.isAccessible = true
                        try {
                            method.javaMethod?.invoke(plugin, event)
                        } catch (e: Exception) {
                            logger.log("Error calling method ${method.parameters}: ${e.message}", SEVERE)
                            e.printStackTrace()
                        }
                    }
                }
                Annotype.ADVANCED -> {
                    privateHandlerMethods.forEach { method ->
                        method.isAccessible = true
                        try {
                            method.call(plugin, event)
                        } catch (e: Exception) {
                            logger.log("Error calling method ${method}: ${e.message}", SEVERE)
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
    enum class Annotype {
        PLAIN,
        ADVANCED
    }

    fun loadPluginConfig(key: String, jarPath: String): Any? {
        val jarFile = JarFile(jarPath)
        var configYmlRaw: String? = null
        val entries: Enumeration<JarEntry> = jarFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.name == "plugin.yml") {
                val inputStream = jarFile.getInputStream(entry)
                configYmlRaw = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
                break
            }
        }

        if (configYmlRaw == null) {
            throw IllegalArgumentException("Resource 'plugin.yml' not found in JAR: $jarPath")
        }
        val yamlParser = Yaml()
        val configMap: Map<String, Any> = yamlParser.load(configYmlRaw)
        return configMap[key]
    }
}
