package ind.glowingstone

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

class Handler {
    private val app: HttpHandler = routes(
        "/upload" bind Method.POST to ::handleUpload,
        "/" bind Method.GET to ::handleRunning
    )

    private fun handleUpload(request: Request): Response {
        val body = request.bodyString()
        val handler = Deliver()
        //println(body + "\n")
        handler.direct(body)
        return Response(Status.OK).body("")
    }
    private fun handleRunning(req: Request): Response {
        val Pluginnum = PluginManager.Companion.registeredPluginClasses.size
        val Body = "Sci-Bot project running. \n $Pluginnum plugins(handlers) loaded."
        return Response(Status.OK).body(Body)
    }
    fun init() {
        val handler = app
        val cfg = Configurations()
        val server = handler.asServer(SunHttp(cfg.get("server_port") as Int)).start()
        println("Backend Service started on port ${server.port()}.")
    }
}
