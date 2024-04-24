package ind.glowingstone

class Events {
    data class PicMessage(val url:String, val sender: Long)
    data class PlainMessage(val message: String, val sender: Long)
}