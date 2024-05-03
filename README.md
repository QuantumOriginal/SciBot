# Sci-Bot
基于Onebot-11协议的机器人插件实现。

本后端可以和基于onebot11协议的前端进行对接，支持编写插件来获取，发送消息。

一个很简单的监听器示例
```Kotlin
import ind.glowingstone.MessageConstructor
import java.util.logging.Level

class PluginMain : Plugin {
    var sender: SimpleSender? = null
    var logger: SimpleLogger? = null
    /*
    This is the sample plugin of SciBot.
    Define Main class and Plugin Name in resources/plugin.yml
    use @PlainHandler to create an event handler.
    implements Plugin interface to create a main class.
     */
    override suspend fun start(logger: SimpleLogger, sender: SimpleSender) {
        logger.log("hello,World", Level.INFO)
        this.sender = sender
        this.logger = logger
        sender.plainSend("hello,world", Sender.Type.GROUP,10000)
    }
    @Annonations.PlainHandler(MessageConstructor.Types.PLAIN)
    fun doSomething(event: Events.MajorEvent) {
        println("MyPlugin called")
        for (any in event.msgArr) {
            if(any is Events.PlainMessage) {
                println("recived message: ${any.message} from ${event.sender.uid}")
            }
        }
    }
}
```
它支持根据类型监听Bot事件，私聊/群消息获取/发送，基于插件的可拓展功能。
同时，本后端提供了MessageConstructor来快速组装消息段。
```Kotlin
val simpleArr: MutableList<Any> = mutableListOf(
    Events.PlainMessage("你好，世界")
)
messageConstructor.factory(simpleArr)
```
使用这个函数来快速将多个不同类型的Segments结合到一起。
支持基于yml的个性化config:
```yml
configuration_version: 0.1, 
server_port: 25565,
upload_url: 'http://localhost:8080',
log-dir: ./logs, 
debug: true, 
auth: 123456

```
更多插件API正在开发中，该项目由于没有完全实现Onebot的所有Message处理而暂未处于可用阶段，您可以选择先行自行编译来体验部分功能。
