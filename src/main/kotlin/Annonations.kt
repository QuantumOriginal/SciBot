package ind.glowingstone

class Annonations {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    annotation class PlainHandler(val type: MsgTypes = MsgTypes.PLAIN)
    enum class MsgTypes {
        PLAIN,
        IMG,
        FACE,
        RECORD,
        VIDEO,
        AT,
        SHARE,
        REPLY
    }
}