package ind.glowingstone

import Events
import Logger
import PluginManager
import Sender
import org.http4k.client.JavaHttpClient
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then
import java.io.ObjectInputFilter.Config
import java.lang.invoke.MethodHandles.loop
import java.util.logging.Level

val printResponseBodyFilter = Filter { next ->
    { request: Request ->
        val response = next(request)
        val responseBody = response.bodyString()
        response
    }
}
val client: HttpHandler = JavaHttpClient()
suspend fun main(){
    val logger: Logger = Logger("MAIN")
    val cfg: Configurations = Configurations();
    val web = Handler()
    if (cfg.get("auth")?.equals("YOUR_KEY_HERE") == true){
        logger.log("Warning: Sci-Bot is starting without an auth key. Requests without an auth key may be vulnerable.", Level.WARNING)
    }
    val pluginMgr = PluginManager("plugins")
    Host.pluginMgr = pluginMgr
    web.init()
    val deliver = Deliver()
    Host.pluginMgr!!.getDisabledPlugins()
    Host.pluginMgr!!.loadPlugins()
}
class Host{
    companion object{
        var pluginMgr: PluginManager? = null
        val HOST_VERSION = 0.1
        val QClient = printResponseBodyFilter.then(client)
        val configInstance = Configurations()
    }
}