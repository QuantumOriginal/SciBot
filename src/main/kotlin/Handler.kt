package ind.glowingstone

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

class Handler {
    private val app: HttpHandler = routes(
        "/upload" bind Method.POST to ::handleUpload
    )

    private fun handleUpload(request: Request): Response {
        val body = request.bodyString()
        val handler = Deliver()
        //println(body + "\n")
        handler.direct(body)
        return Response(Status.OK).body("")
    }

    init {
        val handler = app
        val server = handler.asServer(SunHttp(9912)).start()
        println("Server started on port ${server.port()}")
    }
}
