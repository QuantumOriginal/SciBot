package ind.glowingstone

import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class Loader {
    fun call(instance: Any, type: Annonations.MsgTypes, arg: String) {
        val functions = instance::class.declaredFunctions
        for (function in functions) {
            val annotation = function.findAnnotation<Annonations.PlainHandler>()
            if (annotation?.type == type && annotation != null ) {
                function.call(instance, arg)
            }
        }
    }
}