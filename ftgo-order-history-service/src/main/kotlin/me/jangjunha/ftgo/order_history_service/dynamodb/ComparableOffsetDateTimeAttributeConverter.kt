package me.jangjunha.ftgo.order_history_service.dynamodb

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.TypeConvertingVisitor
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class ComparableOffsetDateTimeAttributeConverter : AttributeConverter<OffsetDateTime> {
    override fun transformFrom(input: OffsetDateTime?): AttributeValue {
        return AttributeValue.fromN(input?.toInstant()?.toEpochMilli()?.toString())
    }

    override fun transformTo(input: AttributeValue?): OffsetDateTime {
        return try {
            if (input?.n() != null) {
                EnhancedAttributeValue.fromString(input.n()).convert(Visitor)
            } else {
                EnhancedAttributeValue.fromAttributeValue(input).convert(Visitor)
            }
        } catch (e: RuntimeException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun type(): EnhancedType<OffsetDateTime> {
        return EnhancedType.of(OffsetDateTime::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.N
    }

    private object Visitor : TypeConvertingVisitor<OffsetDateTime>(
        OffsetDateTime::class.java,
        ComparableOffsetDateTimeAttributeConverter::class.java,
    ) {
        override fun convertString(value: String?): OffsetDateTime {
            return Instant.ofEpochMilli(value!!.toLong()).atOffset(ZoneOffset.UTC)
        }
    }
}
