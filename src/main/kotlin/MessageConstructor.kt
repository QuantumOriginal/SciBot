package ind.glowingstone

import org.json.JSONArray
import org.json.JSONObject

class MessageConstructor {
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

    data class MsgSeg(val type: Types, val msg: String)

    fun create(type: Types, msg: String): MsgSeg {
        return MsgSeg(type, msg)
    }

    fun factory(msgs: Collection<MsgSeg>): JSONArray {
        if (msgs.isEmpty()) {
            throw IllegalArgumentException("Message segments cannot be empty")
        }

        val msgArr = JSONArray()
        for (msg in msgs) {
            val msgObj = createMsgObj(msg.type)
            val dataObj = createDataObj(msg.type, msg.msg)
            msgObj.put("data", dataObj)
            msgArr.put(msgObj)
        }
        return msgArr
    }
    fun factory(vararg msgs: MsgSeg): JSONArray {
        if (msgs.isEmpty()) {
            throw IllegalArgumentException("Message segments cannot be empty")
        }

        val msgArr = JSONArray()
        for (msg in msgs) {
            val msgObj = createMsgObj(msg.type)
            val dataObj = createDataObj(msg.type, msg.msg)
            msgObj.put("data", dataObj)
            msgArr.put(msgObj)
        }
        return msgArr
    }

    private fun createMsgObj(type: Types): JSONObject {
        val msgObj = JSONObject()
        msgObj.put("type", getMessageType(type))
        return msgObj
    }

    private fun createDataObj(type: Types, msg: String): JSONObject {
        val dataObj = JSONObject()
        val dataFieldName = getConsts(type)
        dataObj.put(dataFieldName, msg)
        return dataObj
    }

    private fun getMessageType(type: Types): String {
        return when (type) {
            Types.PLAIN -> "text"
            Types.IMG -> "image"
            Types.FACE -> "face"
            Types.RECORD -> "record"
            Types.VIDEO -> "video"
            Types.AT -> "at"
            Types.SHARE -> "share"
            Types.REPLY -> "reply"
        }
    }

    private fun getConsts(type: Types): String {
        return when (type) {
            Types.PLAIN -> "text"
            Types.IMG -> "file"
            Types.FACE -> "id"
            Types.RECORD -> "file"
            Types.VIDEO -> "file"
            Types.AT -> "qq"
            Types.SHARE -> "url"
            Types.REPLY -> "id"
        }
    }
}
