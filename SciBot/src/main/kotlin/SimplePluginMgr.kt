import SimpleScheduler.Companion.jobList
import ind.glowingstone.Host
import ind.glowingstone.HostExposedToPlugins
import ind.glowingstone.MessageConstructor
import kotlinx.coroutines.*
import org.scibot.Annonations
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.net.URLClassLoader
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.logging.Level
import java.util.logging.Level.SEVERE
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import org.scibot.Interfaces.*
import org.scibot.Annonations.*
import org.scibot.HostOperations
import org.scibot.InjectableTypes
import org.scibot.Interfaces
import kotlin.reflect.KClass
import kotlin.reflect.full.*

class PluginManager(private val pluginDirectory: String) {

    class PluginManifests{
        var name:String = ""
        var version: String = ""
        var author: String? = ""
        var description: String? = ""
        var repository: String? = ""
    }
    val logger: Logger = Logger("PluginManager")
    val pluginClassInstances: MutableSet<KClass<out Any>> = mutableSetOf()
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

    fun getPluginList() {
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        val pluginsInMarket : MutableList<PluginManifests> = mutableListOf()
        jarFiles?.forEach { jarFile ->
        try {
                val pluginManifest = PluginManifests()
                val configmap = loadPluginConfipMapper(jarFile.toString())
                if (configmap.contains("version") && configmap.contains("plugin-name")){
                    pluginManifest.name = configmap["plugin-name"].toString()
                    pluginManifest.version = configmap["version"].toString()
                } else {
                    logger.log("plugin ${jarFile.toString()} doesn't contains a correct plugin manifest.")
                }
                pluginsInMarket.add(pluginManifest)
            } catch (e: Exception){

            }
        }
    }

    suspend fun loadPlugins() {
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        jarFiles?.forEach { jarFile ->
            try {
                val mainClassName = loadPluginConfig("main-class", jarFile.toString()).toString()
                val pluginClass = classLoader.loadClass(mainClassName)
                if (Plugin::class.java.isAssignableFrom(pluginClass)) {
                    val pluginInstance = pluginClass.kotlin.createInstance() as Plugin
                    loadedPlugins.add(pluginInstance)
                    val loggerName = loadPluginConfig("plugin-name", jarFile.toString()).toString()
                    val pluginLogger = Logger(loggerName)
                    val pluginSender = Sender()
                    val pluginHost: HostOperations = HostExposedToPlugins()
                    val markedInjectableMethods = pluginInstance.javaClass.methods.filter { it.isAnnotationPresent(Inject::class.java) }
                    markedInjectableMethods.forEach { method ->
                        method.isAccessible = true
                        val injectAnnotation = method.getAnnotation(Inject::class.java)

                        val injectionTarget = when (injectAnnotation?.type) {
                            InjectableTypes.LOGGER -> pluginLogger
                            InjectableTypes.SENDER -> pluginSender
                            InjectableTypes.HOST -> pluginHost
                            null -> {
                                logger.log("ERROR: ${method.name}() 没有指定要注入的类型.")
                                return@forEach
                            }
                        }

                        try {
                            method.invoke(pluginInstance, injectionTarget)
                            logger.debug("成功注入 ${injectAnnotation.type} 到方法 ${method.name}")
                        } catch (e: Exception) {
                            logger.log("注入失败: 方法 ${method.name} 无法被调用: ${e.message}", SEVERE)
                            e.printStackTrace()
                        }
                    }
                    pluginInstance.start()
                }

                JarFile(jarFile).use { jar ->
                    val entries = jar.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        if (entry.name.endsWith(".class")) {
                            val className = entry.name.replace("/", ".").removeSuffix(".class")
                            val clazz = classLoader.loadClass(className).kotlin
                            val instance = try {
                                clazz.createInstance()
                            } catch (e: Exception) {
                                null
                            }
                            instance?.let {
                                pluginClassInstances.add(it as KClass<out Any>)
                                TaskScheduler.scheduleTasks(it)
                            }
                        }
                    }
                }
            } catch(e: IncompatibleClassChangeError) {
                logger.log("插件 ${jarFile.name} 不兼容当前的SciBot框架，请检查java环境。", Level.SEVERE)
            } catch (e: Exception) {
                logger.log("插件 ${jarFile.name} 的 start 方法出现错误: ${e.message}", Level.SEVERE)
                e.printStackTrace()
            }
        }
    }


    fun invokePluginMethod(type: Annotype, event: org.scibot.Events.MajorEvent, args: MessageConstructor.Types) {
        logger.log("Loaded plugins: ${loadedPlugins.size}")
        logger.debug("-------------------BEGIN INVOKE-------------------")

        pluginClassInstances.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<GroupHandler>() != null }

            logger.debug("PlainHandler methods: ${plainHandlerMethods.size}")

            plainHandlerMethods.forEach { method ->
                logger.debug("Checking PlainHandler method: ${method.name}()")
            }

            val privateHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<PrivateHandler>() != null}

            logger.debug("PrivateHandler methods: ${privateHandlerMethods.size}")

            privateHandlerMethods.forEach { method ->
                logger.debug("Checking PrivateHandler method: ${method.name}()")
            }

            when (type) {
                Annotype.PLAIN -> {
                    logger.debug("Invoking GroupHandler methods")

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

       requireNotNull(configYmlRaw) {
            "Resource 'plugin.yml' not found in JAR: $jarPath"
        }
        val yamlParser = Yaml()
        val configMap: Map<String, Any> = yamlParser.load(configYmlRaw)
        return configMap[key]
    }
    fun loadPluginConfipMapper(jarPath: String): Map<String,Any> {
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
        requireNotNull(configYmlRaw) {
            "Resource 'plugin.yml' not found in JAR: $jarPath"
        }
        val yamlParser = Yaml()
        val configMap: Map<String, Any> = yamlParser.load(configYmlRaw)
        return configMap
    }
    object TaskScheduler {
        suspend fun scheduleTasks(pluginInstance: Any) {
            val kClass = pluginInstance::class

            for (function in kClass.functions) {
                val scheduler = function.findAnnotation<Annonations.Scheduler>()
                if (scheduler != null) {
                    val job = CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                        while (isActive) {
                            function.call(pluginInstance)
                            delay(scheduler.interval)
                        }
                    }
                    jobList.add(job)
                }
            }
        }
    }
}
