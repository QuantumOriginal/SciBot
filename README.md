# Sci-Bot
基于Onebot-11协议的机器人插件实现。

本后端可以和基于onebot11协议的前端进行对接，支持编写插件来获取，发送消息。

一个很简单的监听器示例
```Kotlin
import ind.glowingstone.MessageConstructor
import java.util.logging.Level
class PluginMain : Plugin {
    override fun start(logger: SimpleLogger) {
        logger.log("Hello,World", Level.INFO)
    }
    @Annonations.PlainHandler(MessageConstructor.Types.PLAIN)
    fun doSomething(event: Events.MajorEvent) {
        println("recived plain message")
        println("recived: ${event.message}")
    }
}
```
它支持根据类型监听Bot事件，私聊/群消息获取/发送，基于插件的可拓展功能。
同时，本后端提供了MessageConstructor来快速组装消息段。
```Kotlin
messageConstructor.factory(MessageConstructor.MsgSeg(MessageConstructor.Types.PLAIN, content)
```
使用这个函数来快速将多个不同类型的Segments结合到一起。
支持基于yml的个性化config:
```yml
server_port: 8080,
upload_url: localhost:8080,
log-dir: "./logs",
```
更多插件API正在开发中，该项目由于没有完全实现Onebot的所有Message处理而暂未处于可用阶段，您可以选择先行自行编译来体验部分功能。
