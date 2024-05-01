import ind.glowingstone.Annonations
import java.io.File
import java.net.URLClassLoader
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
                val pluginClass = classLoader.loadClass("Plugin")
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
                            println("Error calling method ${method.parameters}: ${e.message}")
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
                            println("Error calling method ${method}: ${e.message}")
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
