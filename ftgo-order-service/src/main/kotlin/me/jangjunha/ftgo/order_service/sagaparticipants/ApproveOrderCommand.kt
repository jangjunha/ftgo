package me.jangjunha.ftgo.order_service.sagaparticipants

import java.util.UUID

data class ApproveOrderCommand(
    override val orderId: UUID
): OrderCommand(orderId) {
    protected constructor(): this(UUID(0, 0))
}
