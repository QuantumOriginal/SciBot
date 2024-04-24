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
                private(msgObject)
            }
        }
    }
    fun group(body: JSONObject) {
        val loader = Loader()
        val sender = body.getJSONObject("sender")
        val uid: Long = sender.getLong("user_id")
        val nickname: String = sender.getString("nickname")
        val msgTypeObj: JSONObject = body.getJSONArray("message").getJSONObject(0)
        val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
        when(msgTypeObj.getString("type")){
            "text" -> {
                val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"),uid)
                //loader.call(event,Annonations.MsgTypes.PLAIN, event)
                callPlugins(Annonations.MsgTypes.PLAIN, event)
            }
        }
    }
    fun callPlugins(type: Annonations.MsgTypes, arg: Any){
        for (registeredPluginClass in registeredPluginClasses) {
            loader.call(registeredPluginClass, type, arg)
        }
    }
    fun private(body: JSONObject) {
    }
}