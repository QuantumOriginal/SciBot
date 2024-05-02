package ind.glowingstone

import SimpleLogger
import SimpleSender
import org.http4k.core.Method
import org.http4k.core.Response
import org.jetbrains.annotations.Nullable
import org.json.JSONObject

class Sender : SimpleSender{
    val messageConstructor = MessageConstructor()
    val cfg = Configurations()
    override fun send(msgArrs: MutableList<MessageConstructor.MsgSeg>,operation: Type, id: Long){
        val msgObj = JSONObject()
        val urlEndpoint = ""
        if (operation == Type.PRIVATE) {
            "${cfg.get("upload_url")}/send_private_msg"
            msgObj.put("user_id", id)
        } else if (operation == Type.GROUP) {
            "${cfg.get("upload_url")}/send_group_msg"
            msgObj.put("group_id", id)
        }
        msgObj.put("message", messageConstructor.factory(msgArrs))
        msgObj.put("auto_escape", false)
        val response: Response = QClient(org.http4k.core.Request(Method.POST, urlEndpoint).body(msgObj.toString()))
    }
    override fun plainSend(content: String, operation: Type, id: Long) {
        val msgObj = JSONObject()
        val urlEndpoint = ""
        if (operation == Type.PRIVATE) {
            "${cfg.get("upload_url")}/send_private_msg"
            msgObj.put("user_id", id)
        } else if (operation == Type.GROUP) {
            "${cfg.get("upload_url")}/send_group_msg"
            msgObj.put("group_id", id)
        }
        val emptyArr: MutableList<MessageConstructor.MsgSeg> = mutableListOf()
        emptyArr.addLast(MessageConstructor.MsgSeg(MessageConstructor.Types.PLAIN, content))
        msgObj.put("message", messageConstructor.factory(emptyArr))
        msgObj.put("auto_escape", false)
        val response: Response = QClient(org.http4k.core.Request(Method.POST, urlEndpoint).body(msgObj.toString()))
        println(response.bodyString())
    }
    enum class Type{
        PRIVATE,
        GROUP
    }
}