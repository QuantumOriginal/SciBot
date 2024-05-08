import Logger
import ind.glowingstone.MessageConstructor
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.net.URLClassLoader
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.logging.Level.SEVERE
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod

interface Plugin {
    suspend fun start(Logger: SimpleLogger, sender: SimpleSender)
}

class PluginManager(private val pluginDirectory: String) {
    val logger: Logger = Logger("PluginManager")
    companion object{
        val loadedPlugins = mutableListOf<Plugin>()
        val disabledPlugins = mutableListOf<String>()
    }
    val pluginDir = File(pluginDirectory)
    private val classLoader: URLClassLoader
    val disabledJarFiles = pluginDir.listFiles { _, name -> name.endsWith(".disabled")}
    val disabledJarUrls = disabledJarFiles?.map { it.toURI().toURL() }?.toTypedArray()
    init {
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        val jarUrls = jarFiles?.map { it.toURI().toURL() }?.toTypedArray()
        classLoader = URLClassLoader(jarUrls)
        logger.log("Started.")
    }
    fun getDisabledPlugins(){
        disabledJarFiles?.forEach { jarFile ->
            try {
                val pluginName = loadPluginConfig("plugin-name", jarFile.toString()).toString()
                disabledPlugins.add(pluginName)
            } catch (e: Exception) {
                logger.log("无法确认被禁用插件的信息 ${jarFile.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    suspend fun loadPlugins() {
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
                logger.log("插件 ${jarFile.name}的start方法出现错误: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun invokePluginMethod(type: Annotype, event: Events.MajorEvent, args: MessageConstructor.Types) {
        logger.log("Loaded plugins: ${loadedPlugins.size}")
        logger.debug("-------------------BEGIN INVOKE-------------------")

        loadedPlugins.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PlainHandler>() != null }

            logger.debug("PlainHandler methods: ${plainHandlerMethods.size}")

            plainHandlerMethods.forEach { method ->
                logger.debug("Checking PlainHandler method: ${method.name}()")
            }

            val privateHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PrivateHandler>() != null}

            logger.debug("PrivateHandler methods: ${privateHandlerMethods.size}")

            privateHandlerMethods.forEach { method ->
                logger.debug("Checking PrivateHandler method: ${method.name}()")
            }

            when (type) {
                Annotype.PLAIN -> {
                    logger.debug("Invoking PlainHandler methods")

                    plainHandlerMethods.forEach { method ->
                        method.isAccessible = true
                        try {
                            logger.debug("Invoking method ${method.name}() with parameters ${method.parameters.map { it.type }}")
                            method.javaMethod?.invoke(plugin, event)
                        } catch (e: Exception) {
                            logger.log("Error calling method ${method.name}(): ${e.message}", SEVERE)
                            e.printStackTrace()
                        }
                    }
                }
                Annotype.ADVANCED -> {
                    logger.debug("Invoking PrivateHandler methods")

                    privateHandlerMethods.forEach { method ->
                        method.isAccessible = true
                        try {
                            logger.debug("Invoking method ${method.name}() with parameters ${method.parameters.map { it.type }}")
                            method.call(plugin, event)
                        } catch (e: Exception) {
                            logger.log("Error calling method ${method.name}(): ${e.message}", SEVERE)
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        logger.debug("--------------------END INVOKE--------------------")
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
