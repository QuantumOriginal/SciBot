package ind.glowingstone

import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class Loader {
    fun call(instance: Any, type: MessageConstructor.Types, arg: Any, isPrivate: Boolean) {
        val functions = instance::class.declaredFunctions
        for (function in functions) {
            val annotation = if (isPrivate) {
                function.findAnnotation<Annonations.PrivateHandler>()
            } else {
                function.findAnnotation<Annonations.PlainHandler>()
            }
            if (annotation != null){
                try {
                    function.call(instance, arg)
                } catch (e: Exception) {
                    println("Error while calling function: ${e.message}")
                }
            }
        }
    }
}
