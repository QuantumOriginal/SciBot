import Logger
import SimpleSender
import ind.glowingstone.Configurations
import ind.glowingstone.Host.Companion.QClient
import ind.glowingstone.MessageConstructor
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.json.JSONObject
import kotlinx.coroutines.*
import java.util.logging.Level

class Sender : SimpleSender {
    private val logger = Logger("Sender")
    private val messageConstructor = MessageConstructor()
    private val cfg = Configurations()
    private fun sendAsync(request: org.http4k.core.Request, callback: (Response) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = QClient(request)
                withContext(Dispatchers.Main) {
                    callback(response)
                }
            } catch (e: Exception) {
                logger.log("Exception occurred while sending request: ${e.message}", Level.SEVERE)
            }
        }
    }

    override fun send(
        msgArrs: MutableList<MessageConstructor.MsgSeg>,
        operation: Type,
        id: Long
    ) {
        val msgObj = JSONObject()
        val urlEndpoint: String = when (operation) {
            Type.PRIVATE -> "${cfg.get("upload_url")}/send_private_msg"
            Type.GROUP -> "${cfg.get("upload_url")}/send_group_msg"
        }

        msgObj.put("message", messageConstructor.factory(msgArrs))
        msgObj.put("auto_escape", false)
        if (operation == Type.PRIVATE) {
            msgObj.put("user_id", id)
        } else {
            msgObj.put("group_id", id)
        }

        val request = org.http4k.core.Request(Method.POST, urlEndpoint).body(msgObj.toString())
        sendAsync(request) { response ->
            if (response.status != Status.OK) {
                logger.log("ERROR POST message to Server. Status: ${response.status}, Description: ${response.status.description}", Level.SEVERE)
            }
        }
    }

    override fun plainSend(content: String, operation: Type, id: Long) {
        val msgObj = JSONObject()
        val urlEndpoint: String = when (operation) {
            Type.PRIVATE -> "${cfg.get("upload_url")}/send_private_msg"
            Type.GROUP -> "${cfg.get("upload_url")}/send_group_msg"
        }

        val emptyArr: MutableList<MessageConstructor.MsgSeg> = mutableListOf(
            MessageConstructor.MsgSeg(MessageConstructor.Types.PLAIN, content)
        )

        msgObj.put("message", messageConstructor.factory(emptyArr))
        msgObj.put("auto_escape", false)
        if (operation == Type.PRIVATE) {
            msgObj.put("user_id", id)
        } else {
            msgObj.put("group_id", id)
        }

        val request = org.http4k.core.Request(Method.POST, urlEndpoint).body(msgObj.toString())
        sendAsync(request) { response ->
            if (response.status != Status.OK) {
                logger.log("ERROR POST message to Server. Status: ${response.status}, Description: ${response.status.description}", Level.SEVERE)
            }
        }
    }

    enum class Type {
        PRIVATE,
        GROUP
    }
}
