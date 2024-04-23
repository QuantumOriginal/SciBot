package ind.glowingstone

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
    }
    fun private(body: JSONObject) {
    }
}