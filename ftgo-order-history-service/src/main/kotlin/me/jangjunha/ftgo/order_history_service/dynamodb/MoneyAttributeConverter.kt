package me.jangjunha.ftgo.order_history_service.dynamodb

import me.jangjunha.ftgo.common.Money
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.TypeConvertingVisitor
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object MoneyAttributeConverter : AttributeConverter<Money> {
    override fun transformFrom(input: Money?): AttributeValue {
        return AttributeValue.fromS(input?.amount.toString())
    }

    override fun transformTo(input: AttributeValue?): Money {
        return try {
            if (input?.s() != null) {
                EnhancedAttributeValue.fromString(input.s()).convert(Visitor)
            } else {
                EnhancedAttributeValue.fromAttributeValue(input).convert(Visitor)
            }
        } catch (e: RuntimeException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun type(): EnhancedType<Money> {
        return EnhancedType.of(Money::class.java)
    }

    override fun attributeValueType(): AttributeValueType {
        return AttributeValueType.S
    }

    private object Visitor : TypeConvertingVisitor<Money>(
        Money::class.java,
        MoneyAttributeConverter::class.java,
    ) {
        override fun convertString(value: String?): Money {
            return Money(value)
        }
    }
}
