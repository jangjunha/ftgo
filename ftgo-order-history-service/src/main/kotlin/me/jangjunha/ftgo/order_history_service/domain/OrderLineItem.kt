package me.jangjunha.ftgo.order_history_service.domain

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_history_service.dynamodb.FtgoAttributeConverterProvider
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import me.jangjunha.ftgo.order_service.api.OrderLineItem as OrderLineItemAPI

@DynamoDbBean(
    converterProviders = [
        FtgoAttributeConverterProvider::class,
        DefaultAttributeConverterProvider::class,
    ]
)
data class OrderLineItem(
    var quantity: Int = 1,
    var menuItemId: String = "",
    var name: String = "",
    var price: Money = Money.ZERO,
) {
    companion object {
        fun from(li: OrderLineItemAPI): OrderLineItem {
            return OrderLineItem(
                li.quantity,
                li.menuItemId,
                li.name,
                li.price,
            )
        }
    }
}
