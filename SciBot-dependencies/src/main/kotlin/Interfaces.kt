package org.scibot

import org.scibot.Events
import java.util.logging.Level

class Interfaces {
    interface Plugin {
        suspend fun start()
    }
    interface SimpleLogger{
        fun log(msg: String, level: Level? = Level.INFO)
        fun debug(msg: String)
    }
    interface SimpleSender{
        suspend fun plainSend(content: String, operation: Sender.Type, id: Long)
        suspend fun send(msgArrs: MutableList<Events>, operation: Sender.Type, id: Long)
    }
}

class Sender{
    enum class Type {
        PRIVATE,
        GROUP
    }
}