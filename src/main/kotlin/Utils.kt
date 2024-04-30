package ind.glowingstone

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
    fun determineType(list: MutableList<Any>): MessageConstructor.Types {
        return when {
            list.any { it is Events.AtMessage } -> MessageConstructor.Types.AT
            list.any { it is Events.PicMessage } -> MessageConstructor.Types.IMG
            else -> MessageConstructor.Types.PLAIN
        }
    }

}