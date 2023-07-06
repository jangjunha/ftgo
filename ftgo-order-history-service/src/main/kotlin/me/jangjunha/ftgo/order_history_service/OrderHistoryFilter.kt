package me.jangjunha.ftgo.order_history_service

import me.jangjunha.ftgo.order_service.api.OrderState
import java.time.OffsetDateTime

data class OrderHistoryFilter(
    val since: OffsetDateTime = OffsetDateTime.now().minusDays(30),
    val status: OrderState? = null,
    val keywords: Set<String> = emptySet(),
    val startKeyToken: String? = null,
    val pageSize: Int? = null,
)
