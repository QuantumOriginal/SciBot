package org.scibot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.scibot.Events
import java.util.concurrent.TimeUnit
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
    interface Scheduler{
        suspend fun schedule(operation: Scheduler.() -> Unit, interval: Long, unit: TimeUnit, dispatcher: MainCoroutineDispatcher? = Dispatchers.Main)
        fun close()
    }
}

class Sender{
    enum class Type {
        PRIVATE,
        GROUP
    }
}