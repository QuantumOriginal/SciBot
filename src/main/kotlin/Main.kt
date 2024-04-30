package ind.glowingstone

import PluginManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.http4k.client.JavaHttpClient
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then

val printResponseBodyFilter = Filter { next ->
    { request: Request ->
        val response = next(request)
        val responseBody = response.bodyString()
        response
    }
}
val client: HttpHandler = JavaHttpClient()
val QClient = printResponseBodyFilter.then(client)
fun main(){
    val cfg: Configurations = Configurations();
    val web: Handler = Handler()
    val pluginMgr = PluginManager("plugins")
    web.init()
    pluginMgr.loadPlugins()
}