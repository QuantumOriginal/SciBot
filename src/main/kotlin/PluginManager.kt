package ind.glowingstone

import java.io.File
import java.net.URL
import java.net.URLClassLoader

internal interface Plugin {
    fun start()
}
class PluginManager {
    companion object {
        val registeredPluginClasses = mutableSetOf<Any>()
    }
    private val PLUGIN_DIRECTORY = "plugins"
    fun load() {
        val pluginDir = File(PLUGIN_DIRECTORY)
        if (!pluginDir.exists() || !pluginDir.isDirectory) {
            pluginDir.mkdirs()
        }

        val jarFiles = pluginDir.listFiles { _, name -> name.endsWith(".jar") }

        if (jarFiles == null || jarFiles.isEmpty()) {
            // DO NOTHING
            return
        }

        val pluginList = mutableListOf<Plugin>()
        for (jarFile in jarFiles) {
            try {
                val jarUrl = jarFile.toURI().toURL()
                val classLoader = URLClassLoader(arrayOf(jarUrl))
                val pluginClass = classLoader.loadClass("Plugin")
                if (Plugin::class.java.isAssignableFrom(pluginClass)) {
                    val pluginInstance = pluginClass.getDeclaredConstructor().newInstance() as Plugin
                    pluginList.add(pluginInstance)
                }
            } catch (e: Exception) {
                println("Err Loading plugin: ${e.message}")
            }
        }
        pluginList.forEach { it.start() }
    }
    fun Reg(handler: Any){
        registeredPluginClasses.add(handler)
    }
}
