package ind.glowingstone

import org.scibot.Events

class Loader {
    fun call(type: MessageConstructor.Types, arg: MutableList<org.scibot.Events>, isPrivate: Boolean, sender: org.scibot.User.Sender) {
        /*
        A Listener should handle:
        A arrayList contains a list of message events
        sender information
         */
        val event : Events.MajorEvent = Events.MajorEvent(sender,arg)
        val plmgr = Host.pluginMgr
        if (plmgr != null) {
            if (isPrivate) {
                plmgr.invokePluginMethod(PluginManager.Annotype.ADVANCED, event, type)
            } else {
                plmgr.invokePluginMethod(PluginManager.Annotype.PLAIN, event, type)
            }
        } else {
            throw Exception("NO pluginmgr init.")
        }
    }
}
