package me.jangjunha.ftgo.order_service

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.MenuItem
import me.jangjunha.ftgo.order_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_service.domain.Restaurant
import java.time.OffsetDateTime
import java.util.UUID

object OrderFixtures {
    val ORDER_ID: UUID = UUID.fromString("43517ead-0606-4d49-98f9-6b6b873b944e")
    val RESTAURANT_ID: UUID = UUID.fromString("d4420ba4-9fa4-4d8e-8e16-a750b9210e82")
    val CONSUMER_ID: UUID = UUID.fromString("0763e858-6a8b-499b-9745-7fc230c54716")

    val A_CAFE: Restaurant = Restaurant(
        RESTAURANT_ID,
        mutableListOf(
            MenuItem("americano", "Americano", Money(1500)),
            MenuItem("latte", "Cafe Latte", Money(2500)),
        ),
        "A Cafe"
    )

    val LATTE_LINE_ITEMS: List<OrderLineItem> = listOf(
        OrderLineItem(1, "latte", "Cafe Latte", Money(2500)),
    )

    val TWO_LATTE_ORDER_DETAILS: OrderDetails = OrderDetails(
        listOf(
            OrderDetails.LineItem(2, "latte", "Cafe Latte", Money(2500)),
        ), Money("5000"), RESTAURANT_ID, CONSUMER_ID
    )

    val DELIVERY_INFO: DeliveryInformation = DeliveryInformation(
        OffsetDateTime.parse("2019-11-24T12:30+09:00"),
        "서울시 강남구 테헤란로 1",
    )
}
