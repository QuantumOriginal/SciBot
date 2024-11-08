package org.scibot

import com.google.gson.Gson
import com.google.gson.JsonParser
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
            if(list[i] is Events.AtMessage){
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
    fun String.determineGender(): Gender {
        return when {
            this.contains("male") -> Gender.MALE
            this.contains("female") -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }
    }
    fun <T> parseDataField(jsonString: String, targetClass: Class<T>): T {
        val gson = Gson()
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val dataObject = jsonObject.getAsJsonObject("data")
        return gson.fromJson(dataObject, targetClass)
    }
}