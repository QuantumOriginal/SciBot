import ind.glowingstone.MessageConstructor
import ind.glowingstone.Sender.Type
import java.util.logging.Level

interface SimpleLogger{
    fun log(msg: String, level: Level? = Level.INFO)
}
interface SimpleSender{
    fun plainSend(content: String, operation: Type, id: Long)
    fun send(msgArrs: MutableList<MessageConstructor.MsgSeg>, operation: Type, id: Long)
}