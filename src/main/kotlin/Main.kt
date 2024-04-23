package ind.glowingstone

import org.http4k.client.JavaHttpClient
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.then
import kotlinx.coroutines.*
import java.lang.Thread.sleep

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
    web.init()
}