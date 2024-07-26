package ind.glowingstone

import org.scibot.Events
import org.json.JSONArray
import org.json.JSONObject

class MessageConstructor {

    fun factory(msgs: Collection<Any>): JSONArray {
        if (msgs.isEmpty()) {
            throw IllegalArgumentException("Message segments cannot be empty")
        }

        val msgArr = JSONArray()
        for (msg in msgs) {
            val msgObj = createMsgObj(msg)
            val dataObj = getConsts(msg)
            msgObj.put("data", dataObj)
            msgArr.put(msgObj)
        }
        return msgArr
    }
    fun factory(vararg msgs: Any): JSONArray {
        if (msgs.isEmpty()) {
            throw IllegalArgumentException("Message segments cannot be empty")
        }

        val msgArr = JSONArray()
        for (msg in msgs) {
            val msgObj = createMsgObj(msg)
            val dataObj = getConsts(msg)
            msgObj.put("data", dataObj)
            msgArr.put(msgObj)
        }
        return msgArr
    }

    private fun createMsgObj(type: Any): JSONObject {
        val msgObj = JSONObject()
        msgObj.put("type", getMessageType(type))
        return msgObj
    }
    fun getMessageType(event: Any): String {
        return when (event) {
            is Events.PicMessage -> "image"
            is Events.AtMessage -> "at"
            is Events.ReplyMessage -> "reply"
            is Events.RecordMessage -> "record"
            is Events.ShareMessage -> "share"
            is Events.FaceMessage -> "face"
            is Events.VideoMessage -> "video"
            is Events.PlainMessage -> "text"
            else -> "text"
        }
    }

    private fun getConsts(type: Any): JSONObject {
        val messageObj: JSONObject = JSONObject()
        when (type) {
            is Events.PicMessage -> {
                messageObj.put("file", type.url)
                if (type.isFlash){
                    messageObj.put("flash", 1)
                } else {
                    messageObj.put("flash", 0)
                }
            }
            is Events.AtMessage -> {
                messageObj.put("qq", type.target)
            }
            is Events.ReplyMessage -> {
                messageObj.put("id", type.id)
            }
            is Events.RecordMessage -> {
                messageObj.put("file", type.url)
                if (type.isMagic){
                    messageObj.put("magic", 1)
                } else {
                    messageObj.put("magic", 0)
                }
            }
            is Events.ShareMessage -> {
                messageObj.put("url", type.url)
                messageObj.put("title", type.title)
                messageObj.put("image", type.image)
                messageObj.put("content", type.content)
            }
            is Events.FaceMessage -> {
                messageObj.put("id", type.id)
            }
            is Events.VideoMessage -> {
                messageObj.put("file", type.url)
            }
            is Events.PlainMessage -> {
                messageObj.put("text", type.message)
            }
            else -> {
                messageObj.put("text", "cannot recognize this")
            }
        }
        return messageObj
    }
    enum class Types {
        PLAIN,
        IMG,
        FACE,
        RECORD,
        VIDEO,
        AT,
        SHARE,
        REPLY
    }
}
