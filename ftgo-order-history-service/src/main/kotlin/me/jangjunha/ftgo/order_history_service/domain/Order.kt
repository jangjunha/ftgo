package me.jangjunha.ftgo.order_history_service.domain

import me.jangjunha.ftgo.order_history_service.dynamodb.ComparableOffsetDateTimeAttributeConverter
import me.jangjunha.ftgo.order_history_service.dynamodb.FtgoAttributeConverterProvider
import me.jangjunha.ftgo.order_service.api.OrderState
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.text.BreakIterator
import java.time.OffsetDateTime
import java.util.*

@DynamoDbBean(
    converterProviders = [
        FtgoAttributeConverterProvider::class,
        DefaultAttributeConverterProvider::class,
    ]
)
data class Order(
    @get:DynamoDbPartitionKey
    var orderId: UUID = UUID(0, 0),
    @get:DynamoDbSecondaryPartitionKey(indexNames = [consumerCreationDateIndexName])
    var consumerId: UUID = UUID(0, 0),
    @get:DynamoDbSecondarySortKey(indexNames = [consumerCreationDateIndexName])
    @get:DynamoDbConvertedBy(ComparableOffsetDateTimeAttributeConverter::class)
    var creationDate: OffsetDateTime = OffsetDateTime.MIN,
    var status: OrderState = OrderState.APPROVAL_PENDING,
    var lineItems: List<OrderLineItem> = emptyList(),
    var restaurantId: UUID = UUID(0, 0),
    var restaurantName: String = "",
) {
    val keywords: Set<String>
        get() =
            tokenize(restaurantName) +
            tokenize(lineItems.map { it.name })

    companion object {
        const val consumerCreationDateIndexName = "ConsumerCreationDateIndex"

        private fun tokenize(text: String): Set<String> {
            val result = HashSet<String>()

            val bi = BreakIterator.getWordInstance()
            bi.setText(text)
            var lastIndex = bi.first()
            while (lastIndex != BreakIterator.DONE) {
                val firstIndex = lastIndex
                lastIndex = bi.next()

                if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text[firstIndex])) {
                    val word = text.substring(firstIndex, lastIndex)
                    result.add(word)
                }
            }

            return result
        }

        private fun tokenize(texts: List<String>): Set<String> {
            return texts.flatMap { tokenize(it) }.toSet()
        }
    }
}
