package ind.glowingstone

import org.http4k.core.Method
import org.http4k.core.Response
import org.jetbrains.annotations.Nullable
import org.json.JSONObject

class Sender {
    val messageConstructor = MessageConstructor()
    val cfg = Configurations()
    fun send(message: String,operation: Type, id: Long){

    }
    fun plainsend(content: String, operation: Type, id: Long) {
        val msgObj = JSONObject()
        if (operation == Type.PRIVATE) {
            msgObj.put("user_id", id)
        } else if (operation == Type.GROUP) {
            msgObj.put("group_id", id)
        }
        msgObj.put("message", messageConstructor.factory(MessageConstructor.MsgSeg(MessageConstructor.Types.PLAIN, content)))
        msgObj.put("auto_escape", false)
        val response: Response = QClient(org.http4k.core.Request(Method.POST, "${cfg.get("upload_url")}/send_group_msg").body(msgObj.toString()))
        println(response.bodyString())
    }
    enum class Type{
        PRIVATE,
        GROUP
    }
}