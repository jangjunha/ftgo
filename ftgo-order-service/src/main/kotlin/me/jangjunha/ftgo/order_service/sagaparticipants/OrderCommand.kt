package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Command
import java.util.UUID

abstract class OrderCommand(
    open val orderId: UUID,
): Command