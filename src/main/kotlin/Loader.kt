package ind.glowingstone

import PluginManager
import java.sql.Types
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class Loader {
    fun call(type: MessageConstructor.Types, arg: Any, isPrivate: Boolean, sender: Events.Sender) {
        /*
        A Listener should handle:
        A arrayList contains a list of message events
        sender information
         */
        val event : Events.MajorEvent = Events.MajorEvent(sender,arg)
        val plmgr = PluginManager("plugins")
        if (isPrivate){
            plmgr.invokePluginMethod(PluginManager.Annotype.ADVANCED,event)
        }
        else {
            plmgr.invokePluginMethod(PluginManager.Annotype.PLAIN, event)
        }

    }
}
