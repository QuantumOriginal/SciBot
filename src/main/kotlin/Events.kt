package ind.glowingstone

import org.jetbrains.annotations.Nullable

class Events {
    data class PicMessage(val url:String, val sender: ind.glowingstone.Events.Sender)
    data class PlainMessage(val message: String, val sender: ind.glowingstone.Events.Sender)
    data class Sender(val uid:Long , val nickname:String, val role:String? = "private")
}