import org.jetbrains.annotations.Nullable

class Events {
    data class PicMessage(val url:String)
    data class AtMessage(val target: Long)
    data class ReplyMessage(val message: String)
    data class RecordMessage(val url: String)
    data class ShareMessage(val url: String, val title: String, val content: String?, val image: String?)
    data class FaceMessage(val id: Int)
    data class VideoMessage(val url: String)
    data class PlainMessage(val message: String)
    data class Sender(val uid:Long , val nickname:String, val role:String? = "private")
    data class MajorEvent(val sender: Sender, val detail: Any)
}