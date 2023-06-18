package me.jangjunha.ftgo.accounting_service.core

import org.springframework.core.ResolvableType
import org.springframework.core.ResolvableTypeProvider

data class EventEnvelope<E : Any>(
    val data: E,
    val metadata: EventMetadata,
): ResolvableTypeProvider {
    override fun getResolvableType(): ResolvableType? {
        return ResolvableType.forClassWithGenerics(
            javaClass,
            ResolvableType.forInstance(data),
        )
    }
}
