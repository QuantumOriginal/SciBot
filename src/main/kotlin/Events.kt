package ind.glowingstone

import org.jetbrains.annotations.Nullable

class Events {
    data class PicMessage(val url:String)
    data class AtMessage(val target: Long)
    data class PlainMessage(val message: String)
    data class Sender(val uid:Long , val nickname:String, val role:String? = "private")
    data class MajorEvent(val sender: Sender, val detail: Any)
}