package org.scibot
import org.scibot.MessageConstructor.Types

class Annonations {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    annotation class GroupHandler(val type:Types = Types.PLAIN)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    annotation class PrivateHandler(val type: Types = Types.PLAIN)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class Scheduler(val interval: Long)

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class Inject(val type: InjectableTypes)
}

enum class InjectableTypes {
    LOGGER,
    SENDER,
    HOST
}
