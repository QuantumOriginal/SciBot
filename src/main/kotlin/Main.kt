package ind.glowingstone

import LogWriter
import Logger
import PluginManager
import Sender
import org.http4k.client.JavaHttpClient
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.system.measureTimeMillis

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
    val timecost = measureTimeMillis {
        logger.log("Sci-Bot loading...", Level.INFO)
        val sender: Sender = Sender()
        if (!sender.testEndpoint()) {
            logger.log("Warning: Specified URL Endpoint isn't accessible. Wait 1000ms to continue...", Level.WARNING)
            Thread.sleep(1000)
        }
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate(LogWriter(), 0, 100, TimeUnit.MILLISECONDS)

        val cfg = Configurations();
        val web = Handler()

        if (cfg.get("auth")?.equals("YOUR_KEY_HERE") == true) {
            logger.log(
                "Warning: Sci-Bot is starting without an auth key. Requests without an auth key may be vulnerable.",
                Level.WARNING
            )
        }
        val pluginMgr = PluginManager("plugins")
        Host.pluginMgr = pluginMgr
        web.init()
        val deliver = Deliver()
        pluginMgr.getDisabledPlugins()
        pluginMgr.loadPlugins()
    }
    logger.log("INIT Success. cost $timecost ms")
}
class Host{
    companion object{
        var pluginMgr: PluginManager? = null
        val HOST_VERSION = 0.1
        val QClient = printResponseBodyFilter.then(client)
        val configInstance = Configurations()
    }
}