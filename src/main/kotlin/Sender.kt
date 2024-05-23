import ind.glowingstone.Configurations
import ind.glowingstone.Host.Companion.QClient
import ind.glowingstone.MessageConstructor
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.json.JSONObject
import kotlinx.coroutines.*
import org.http4k.core.Request
import org.scibot.Interfaces.SimpleSender
import org.scibot.Sender
import java.util.logging.Level

class Sender : SimpleSender {
    private val logger = Logger("Sender")
    private val messageConstructor = MessageConstructor()
    private val cfg = Configurations()

    private val ioDispatcher = SupervisorJob() + Dispatchers.IO
    private val mainDispatcher = Dispatchers.Main

    suspend fun sendAsync(request: org.http4k.core.Request): Response {
        return withContext(ioDispatcher) {
            try {
                QClient(request)
            } catch (e: Exception) {
                logger.log("Exception occurred while sending request: ${e.message}", Level.SEVERE)
                throw e
            }
        }
    }
    suspend fun testEndpoint(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val uploadUrl = cfg.get("upload_url")?.toString() ?: return@withContext false
                val request = Request(Method.GET, uploadUrl)
                val result = QClient(request)
                result.status == Status.OK
            } catch (e: Exception) {
                false
            }
        }
    }
    override suspend fun plainSend(content: String, operation: Sender.Type, id: Long) {
        val msgObj = JSONObject()
        val accessToken = if (cfg.get("auth")?.equals("YOUR_KEY_HERE") == true) {
            ""
        } else {
            "?access_token=${cfg.get("auth")}"
        }
        val urlEndpoint = when (operation) {
            Sender.Type.PRIVATE -> "${cfg.get("upload_url")}/send_private_msg$accessToken"
            Sender.Type.GROUP -> "${cfg.get("upload_url")}/send_group_msg$accessToken"
        }

        val emptyArr: MutableList<Any> = mutableListOf(
            Events.PlainMessage(content)
        )

        msgObj.put("message", messageConstructor.factory(emptyArr))
        msgObj.put("auto_escape", false)
        if (operation == Sender.Type.PRIVATE) {
            msgObj.put("user_id", id)
        } else {
            msgObj.put("group_id", id)
        }

        val request = org.http4k.core.Request(Method.POST, urlEndpoint).body(msgObj.toString())
        val response = sendAsync(request)
        if (response.status != Status.OK) {
            logger.log("ERROR POST message to Server. Status: ${response.status}, Description: ${response.status.description}", Level.SEVERE)
        }
    }

    override suspend fun send(msgArrs: MutableList<org.scibot.Events>, operation: Sender.Type, id: Long) {
        val msgObj = JSONObject()
        val accessToken = if (cfg.get("auth")?.equals("YOUR_KEY_HERE") == true) {
            ""
        } else {
            "?access_token=${cfg.get("auth")}"
        }
        val urlEndpoint = when (operation) {
            Sender.Type.PRIVATE -> "${cfg.get("upload_url")}/send_private_msg$accessToken"
            Sender.Type.GROUP -> "${cfg.get("upload_url")}/send_group_msg$accessToken"
        }

        msgObj.put("message", messageConstructor.factory(msgArrs))
        msgObj.put("auto_escape", false)
        if (operation == Sender.Type.PRIVATE) {
            msgObj.put("user_id", id)
        } else {
            msgObj.put("group_id", id)
        }

        val request = Request(Method.POST, urlEndpoint).body(msgObj.toString())
        val response = sendAsync(request)
        if (response.status != Status.OK) {
            logger.log("ERROR POST message to Server. Status: ${response.status}, Description: ${response.status.description}", Level.SEVERE)
        }
    }
}
