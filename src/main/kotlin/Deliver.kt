package ind.glowingstone
import org.json.JSONArray
import org.json.JSONObject

class Deliver{
    val loader = Loader()
    val utils = Utils()
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
        val msgTypeObjs: JSONArray = body.getJSONArray("message")
        val listsOfMsgSegs = utils.convertJsonArr(msgTypeObjs)
        val msgArrs:MutableList<Any> = ArrayList()
        for (msgTypeObj:JSONObject in listsOfMsgSegs) {
            val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
            when(msgTypeObj.getString("type")) {
                "text" -> {
                    val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"))
                    msgArrs.add(event)
                }
                "at" -> {
                    //well, we don't call a separate event for AT. So you should manually get this func by Util.hasAt().
                    val event: Events.AtMessage = Events.AtMessage(msgDetailObj.getLong("qq"))
                    msgArrs.add(event)
                }
                "image" -> {
                    val event: Events.PicMessage = Events.PicMessage(msgDetailObj.getString("url"))
                    msgArrs.add(event)
                }
            }
        }
        val sender = Events.Sender(uid,nickname, role)
        callPlugins(utils.determineType(msgArrs), msgArrs, false, sender)
    }
    fun callPlugins(type: MessageConstructor.Types, arg: Any, isPriv:Boolean, sender: Events.Sender){
        loader.call(type, arg , isPriv, sender)
    }
    fun priv(body: JSONObject) {
        val msgTypeObjs: JSONArray = body.getJSONArray("message")
        val listsOfMsgSegs = utils.convertJsonArr(msgTypeObjs)
        val msgArrs:MutableList<Any> = ArrayList()
        for (msgTypeObj:JSONObject in listsOfMsgSegs) {
            val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
            when(msgTypeObj.getString("type")) {
                "text" -> {
                    val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"))
                    msgArrs.add(event)
                }
                "image" -> {
                    val event: Events.PicMessage = Events.PicMessage(msgDetailObj.getString("url"))
                    msgArrs.add(event)
                }
            }
        }
        val senderObj = body.getJSONObject("sender")
        val uid: Long = senderObj.getLong("user_id")
        val nickname: String = senderObj.getString("nickname")
        val sender: Events.Sender = Events.Sender(uid,nickname)
        callPlugins(utils.determineType(msgArrs), msgArrs, true, sender)
    }
}