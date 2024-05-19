package ind.glowingstone
import Logger
import PluginManager
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

import java.util.logging.Level.INFO

class Handler {
    val logger: Logger = Logger("BACKEND")
    private val app: HttpHandler = routes(
        "/upload" bind Method.POST to ::handleUpload,
        "/" bind Method.GET to ::handleRunning,
    )

    private fun handleUpload(request: Request): Response {
        val body = request.bodyString()
        val handler = Deliver()
        //println(body + "\n")
        handler.direct(body)
        return Response(Status.OK).body("")
    }
    private fun handleRunning(req: Request): Response {
        val Body = StringBuilder("Sci-Bot project running. \n ${PluginManager.loadedPlugins.size} plugins(handlers) loaded. \n Disabled plugin:")
        if (PluginManager.disabledPlugins.size == 0){
            Body.append("NO DISABLED PLUGINS."+ "\n")
        } else {
            PluginManager.disabledPlugins.forEach() {
                Body.append(it + "\n")
            }
        }
        return Response(Status.OK).body(Body.toString())
    }
    fun init() {
        val handler = app
        val cfg = Configurations()
        val server = handler.asServer(SunHttp(cfg.get("server_port") as Int)).start()
        logger.log("Backend Service started on port ${server.port()}.", INFO)
    }
}
