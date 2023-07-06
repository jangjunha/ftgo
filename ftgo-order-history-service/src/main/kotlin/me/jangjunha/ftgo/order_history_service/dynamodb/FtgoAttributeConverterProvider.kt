package me.jangjunha.ftgo.order_history_service.dynamodb

import me.jangjunha.ftgo.common.Money
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import java.util.concurrent.ConcurrentHashMap

class FtgoAttributeConverterProvider : AttributeConverterProvider {
    private val converters = ConcurrentHashMap<EnhancedType<*>?, AttributeConverter<*>>(
        mapOf(
            Pair(EnhancedType.of(Money::class.java), MoneyAttributeConverter),
        )
    )

    override fun <T : Any?> converterFor(enhancedType: EnhancedType<T>?): AttributeConverter<T>? {
        return converters[enhancedType] as AttributeConverter<T>?
    }
}
