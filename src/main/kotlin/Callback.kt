import ind.glowingstone.MessageConstructor
import java.util.logging.Level

interface SimpleLogger{
    fun log(msg: String, level: Level? = Level.INFO)
}
interface SimpleSender{
    suspend fun plainSend(content: String, operation: Sender.Type, id: Long)
    suspend fun send(msgArrs: MutableList<Any>, operation: Sender.Type, id: Long)
}