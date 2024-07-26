package org.scibot
sealed class Events {
    data class PicMessage(val url:String, val isFlash: Boolean) : Events()
    data class AtMessage(val target: Long) : Events()
    data class ReplyMessage(val id: Long) : Events()
    data class RecordMessage(val url: String, val isMagic: Boolean) : Events()
    data class ShareMessage(val url: String, val title: String, val content: String?, val image: String?) : Events()
    data class FaceMessage(val id: Int) : Events()
    data class VideoMessage(val url: String) : Events()
    data class PlainMessage(val message: String) : Events()
    data class MajorEvent(val sender: User.Sender, val msgArr:MutableList<Events>)
}
class User{
    data class Sender(val uid:Long , val nickname:String, val role:String? = "private")
}