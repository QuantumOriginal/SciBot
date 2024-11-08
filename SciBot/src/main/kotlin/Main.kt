package ind.glowingstone

import LogWriter
import Logger
import PluginManager
import Sender
import com.google.gson.Gson
import ind.glowingstone.Host.Companion.QClient
import ind.glowingstone.Host.Companion.accessToken
import ind.glowingstone.Host.Companion.configInstance
import org.http4k.client.JavaHttpClient
import org.scibot.Utils
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.then
import org.scibot.HostOperations
import org.scibot.Interfaces
import org.scibot.LoginInfo
import org.scibot.StrangerInfo
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
suspend fun main() {
    val logger: Logger = Logger("MAIN")
    val timecost = measureTimeMillis {
        val cfg = Configurations()
        logger.log("Sci-Bot loading...", Level.INFO)
        val sender: Sender = Sender()
        logger.debug("sending request -> ${cfg.get("upload_url")?.toString()}")
        if (!sender.testEndpoint()) {
            logger.log(
                "Warning: Specified URL Endpoint isn't accessible. Wait 1000ms to continue...",
                Level.WARNING
            )
            Thread.sleep(1000)
        }
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate(LogWriter(), 0, 100, TimeUnit.MILLISECONDS)
        val web = Handler()

        if (cfg.get("auth")?.equals("YOUR_KEY_HERE") == true) {
            logger.log(
                "Warning: Sci-Bot is starting without an auth key. Requests without an auth key may be vulnerable.",
                Level.WARNING
            )
        }
        if (cfg.get("enablePluginMarket")?.equals("true") == true) {
            //market.init()
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

class HostExposedToPlugins : HostOperations {
    override fun getCurrentLogin(): LoginInfo = with(Utils()) {
        val request = Request(Method.GET, "${configInstance.get("upload_url")}/get_login_info$accessToken")
        val response = QClient(request).bodyString()
        return parseDataField(response, LoginInfo::class.java)
    }

    override fun getStrangerInfo(userId: Long, cache: Boolean): StrangerInfo = with(Utils()) {
        val request = Request(Method.GET, "${configInstance.get("upload_url")}/get_stranger_info$accessToken&user_id=$userId&no_cache=${!cache}")
        val response = QClient(request).bodyString()
        return parseDataField(response, StrangerInfo::class.java)
    }
}
class Host {

    companion object {
        var pluginMgr: PluginManager? = null
        val HOST_VERSION = 0.1
        val QClient = printResponseBodyFilter.then(client)
        val configInstance = Configurations()
        val accessToken = if (configInstance.get("auth")?.equals("YOUR_KEY_HERE") == true) {
            ""
        } else {
            "?access_token=${configInstance.get("auth")}"
        }
    }
}
