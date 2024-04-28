import ind.glowingstone.Annonations
import ind.glowingstone.Events
import ind.glowingstone.MessageConstructor
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod

interface Plugin {
    fun start()
}

class PluginManager(private val pluginDirectory: String) {
    private val loadedPlugins = mutableListOf<Plugin>()
    private val classLoader: URLClassLoader

    init {
        val pluginDir = File(pluginDirectory)
        require(pluginDir.isDirectory) { "插件目录必须是一个文件夹" }
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        require(!jarFiles.isNullOrEmpty()) { "插件目录中没有找到任何插件" }
        val jarUrls = jarFiles.map { it.toURI().toURL() }.toTypedArray()
        classLoader = URLClassLoader(jarUrls)
    }
    fun loadPlugins() {
        val pluginDir = File(pluginDirectory)
        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }
        jarFiles?.forEach { jarFile ->
            try {
                val pluginClass = classLoader.loadClass("MyPlugin")
                if (Plugin::class.java.isAssignableFrom(pluginClass)) {
                    val pluginInstance = pluginClass.kotlin.createInstance() as Plugin
                    loadedPlugins.add(pluginInstance)
                    pluginInstance.start()
                }
            } catch (e: Exception) {
                println("无法加载插件 ${jarFile.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun invokePluginMethod(type: Annotype, event: Events.MajorEvent) {
        loadedPlugins.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PlainHandler>() != null }

            plainHandlerMethods.forEach { method ->
                println("Method: ${method.name}, Parameters: ${method.parameters.map { it.type }}")
            }
        }

        loadedPlugins.forEach { plugin ->
            val plainHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PlainHandler>() != null }
                .map { it }

            val privateHandlerMethods = plugin::class.declaredMemberFunctions
                .filter { it.findAnnotation<Annonations.PrivateHandler>() != null }
                .map { it }

            when (type) {
                Annotype.PLAIN -> {
                    plainHandlerMethods.forEach { method ->
                        method.isAccessible = true
                        try {
                            method.javaMethod?.invoke(plugin, event)
                        } catch (e: Exception) {
                            println("Error calling method ${method.name}: ${e.message}")
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
                            println("Error calling method ${method.name}: ${e.message}")
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
}
