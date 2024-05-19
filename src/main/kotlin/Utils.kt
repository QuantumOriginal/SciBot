package ind.glowingstone

import org.scibot.Events
import org.json.JSONArray
import org.json.JSONObject

class Utils {
    fun convertJsonArr(arr: JSONArray): MutableList<JSONObject> {
        var list:MutableList<JSONObject> = ArrayList()
        for(i in 0 until arr.length()){
            list.add(arr.get(i) as JSONObject)
        }
        return list
    }
    fun hasAt(list: MutableList<Any>):Boolean{
        for (i in 0 until list.size){
            if(list.get(i) is Events.AtMessage){
                return true
            }
        }
        return false
    }
    fun determineType(list: MutableList<Events>): MessageConstructor.Types {
        return when {
            list.any { it is Events.AtMessage } -> MessageConstructor.Types.AT
            list.any { it is Events.PicMessage } -> MessageConstructor.Types.IMG
            list.any { it is Events.VideoMessage } -> MessageConstructor.Types.VIDEO
            list.any { it is Events.FaceMessage } -> MessageConstructor.Types.FACE
            list.any { it is Events.ReplyMessage } -> MessageConstructor.Types.REPLY
            list.any { it is Events.RecordMessage } -> MessageConstructor.Types.RECORD
            else -> MessageConstructor.Types.PLAIN
        }
    }

}