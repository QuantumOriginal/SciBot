package ind.glowingstone

import ind.glowingstone.PluginManager.Companion.registeredPluginClasses
import org.json.JSONObject

class Deliver{
    val loader = Loader()
    fun direct(body: String) {
        val msgObject = JSONObject(body)
        if (msgObject.has("message_type")) {
            if (msgObject.getString("message_type").equals("group")) {
                group(msgObject)
            } else if (msgObject.getString("message_type").equals("private")) {
                priv(msgObject)
            }
        }
    }
    fun group(body: JSONObject) {
        val loader = Loader()
        val senderObj = body.getJSONObject("sender")
        val uid: Long = senderObj.getLong("user_id")
        val role: String = senderObj.getString("role")
        val nickname: String = senderObj.getString("nickname")
        val msgTypeObj: JSONObject = body.getJSONArray("message").getJSONObject(0)
        val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
        val sender: Events.Sender = Events.Sender(uid,nickname, role)
        when(msgTypeObj.getString("type")){
            "text" -> {
                val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"),sender)
                //loader.call(event,Annonations.MsgTypes.PLAIN, event)
                callPlugins(MessageConstructor.Types.PLAIN, event, false)
            }
        }
    }
    fun callPlugins(type: MessageConstructor.Types, arg: Any, isPriv:Boolean){
        for (registeredPluginClass in registeredPluginClasses) {
            loader.call(registeredPluginClass, type, arg , isPriv)
        }
    }
    fun priv(body: JSONObject) {
        val msgTypeObj: JSONObject = body.getJSONArray("message").getJSONObject(0)
        val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
        val senderObj = body.getJSONObject("sender")
        val uid: Long = senderObj.getLong("user_id")
        val nickname: String = senderObj.getString("nickname")
        val sender: Events.Sender = Events.Sender(uid,nickname)
        when(msgTypeObj.getString("type")) {
            "text" -> {
                val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"), sender)
                callPlugins(MessageConstructor.Types.PLAIN, event, true)
            }
        }
    }
}