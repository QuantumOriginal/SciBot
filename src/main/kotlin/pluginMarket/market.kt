package pluginMarket

import Logger
import ind.glowingstone.Host
import ind.glowingstone.Utils
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import org.http4k.core.Method
import org.json.JSONArray
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path

class market {
    val pm = Host.pluginMgr
    val logger = Logger("PluginMarket")
    val SOURCE_LIST = "plugin_source.json"
    val DEFAULT_URL = "https://scibot.glowingstone.cn/plugins/repo.json"
    val defaultConfig = JSONObject().put("sources",JSONArray().put(DEFAULT_URL))
    suspend fun init(){
        if (Files.notExists(Path.of(SOURCE_LIST)) || Files.isDirectory(Path.of(SOURCE_LIST))){
            Files.createFile(Path.of(SOURCE_LIST))
            logger.log("plugin configuration isn't exist. Using default config...")
            Files.write(Path.of(SOURCE_LIST), defaultConfig.toString().toByteArray())
        }
        val listArr = JSONArray(getSource()!!)
    }
    suspend fun getSource(): String? {
        return Utils().sendRequest(Method.GET, DEFAULT_URL)
    }
}