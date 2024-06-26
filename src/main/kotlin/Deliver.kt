package ind.glowingstone
import org.scibot.Events.*
import org.json.JSONArray
import org.json.JSONObject
import org.scibot.Events

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
        val msgArrs:MutableList<org.scibot.Events> = ArrayList()
        for (msgTypeObj:JSONObject in listsOfMsgSegs) {
            val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
            when(msgTypeObj.getString("type")) {
                "text" -> {
                    val event: Events.PlainMessage = Events.PlainMessage(msgDetailObj.getString("text"))
                    msgArrs.add(event)
                }
                "at" -> {
                    //well, we don't call a separate event for AT. So you should manually get this func by Util.hasAt().
                    val event: AtMessage =  AtMessage(msgDetailObj.getLong("qq"))
                    msgArrs.add(event)
                }
                "image" -> {
                    var event: Events.PicMessage? = null
                        if (msgDetailObj.getBoolean("isFlash")) {
                        event= Events.PicMessage(msgDetailObj.getString("url"), true)
                    } else {
                        event= Events.PicMessage(msgDetailObj.getString("url"), false)
                    }
                    if (event != null) {
                        msgArrs.add(event)
                    }
                }
                "video" -> {
                    val event: Events.VideoMessage = Events.VideoMessage(msgDetailObj.getString("file"))
                    msgArrs.add(event)
                }
            }
        }
        val sender = org.scibot.User.Sender(uid, nickname, role)
        callPlugins(utils.determineType(msgArrs), msgArrs, false, sender)
    }
    fun callPlugins(type: MessageConstructor.Types, arg: MutableList<Events>, isPriv:Boolean, sender: org.scibot.User.Sender){
        loader.call(type, arg , isPriv, sender)
    }
    fun priv(body: JSONObject) {
        val msgTypeObjs: JSONArray = body.getJSONArray("message")
        val listsOfMsgSegs = utils.convertJsonArr(msgTypeObjs)
        val msgArrs:MutableList<Events> = ArrayList()
        for (msgTypeObj:JSONObject in listsOfMsgSegs) {
            val msgDetailObj: JSONObject = msgTypeObj.getJSONObject("data")
            when(msgTypeObj.getString("type")) {
                "text" -> {
                    val event: PlainMessage = PlainMessage(msgDetailObj.getString("text"))
                    msgArrs.add(event)
                }
                "image" -> {
                    var event: PicMessage? = null
                    if (msgDetailObj.getBoolean("isFlash")) {
                        event= PicMessage(msgDetailObj.getString("url"), true)
                    } else {
                        event= PicMessage(msgDetailObj.getString("url"), false)
                    }
                    if (event != null) {
                        msgArrs.add(event)
                    }
                }
                "video" -> {
                    val event: VideoMessage = VideoMessage(msgDetailObj.getString("file"))
                    msgArrs.add(event)
                }
            }
        }
        val senderObj = body.getJSONObject("sender")
        val uid: Long = senderObj.getLong("user_id")
        val nickname: String = senderObj.getString("nickname")
        val sender = org.scibot.User.Sender(uid, nickname)
        callPlugins(utils.determineType(msgArrs), msgArrs, true, sender)
    }
}