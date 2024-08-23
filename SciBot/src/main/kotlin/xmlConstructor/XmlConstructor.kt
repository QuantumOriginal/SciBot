package xmlConstructor

data class Msg(
    var flag: Int = 0,
    var serviceID: Int = 0,
    var brief: String = "",
    var templateID: Int = 0,
    var action: String = ""
) {
    fun toXmlString(): String {
        return "<msg flag='$flag' serviceID='$serviceID' brief='$brief' templateID='$templateID' action='$action'></msg>"
    }
}

class XmlBuilder {
    var header: String = ""
    var msg: Msg = Msg()

    fun header(init: XmlBuilder.() -> Unit) {
        init()
        this.header = "<?xml version='1.0' encoding='utf-8' standalone='yes'?>"
    }

    fun msg(init: Msg.() -> Unit) {
        this.msg = Msg().apply(init)
    }

    fun build(): String {
        return "${header}\n${msg.toXmlString()}"
    }
}

fun xml(init: XmlBuilder.() -> Unit): String {
    val builder = XmlBuilder()
    builder.init()
    return builder.build()
}

fun main() {
    val xmlString = xml {
        header {} // this should be empty
        msg {
            flag = 1
            serviceID = 1
            brief = "foobar"
            templateID = 1
            action = "plugin"
        }
    }

    println(xmlString)
}
