class Events {
    data class PicMessage(val url:String, val isFlash: Boolean)
    data class AtMessage(val target: Long)
    data class ReplyMessage(val id: Long)
    data class RecordMessage(val url: String, val isMagic: Boolean)
    data class ShareMessage(val url: String, val title: String, val content: String?, val image: String?)
    data class FaceMessage(val id: Int)
    data class VideoMessage(val url: String)
    data class PlainMessage(val message: String)
    data class Sender(val uid:Long , val nickname:String, val role:String? = "private")
    data class MajorEvent(val sender: Sender, val msgArr:MutableList<Any>)
}