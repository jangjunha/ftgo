package me.jangjunha.ftgo.accounting_service.core

object EventTypeMapper {
    private var typeMap: HashMap<String, Class<*>> = HashMap()
    private val typeNameMap: HashMap<Class<*>, String> = HashMap()

    fun toName(eventType: Class<*>): String {
        return typeNameMap.computeIfAbsent(eventType) {
            it.typeName.replace("$", "___").replace(".", "__")
        }
    }

    fun toClass(eventTypeName: String): Class<*> {
        return typeMap.computeIfAbsent(eventTypeName) {
            Class.forName(
                it.replace("___", "$").replace("__", ".")
            )
        }
    }
}
