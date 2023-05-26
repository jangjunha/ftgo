package me.jangjunha.ftgo.order_service.sagas.createorder

import io.eventuate.tram.sagas.orchestration.SagaDefinition
import io.eventuate.tram.sagas.simpledsl.SimpleSaga
import me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply
import me.jangjunha.ftgo.order_service.sagaparticipants.AccountingServiceProxy
import me.jangjunha.ftgo.order_service.sagaparticipants.ConsumerServiceProxy
import me.jangjunha.ftgo.order_service.sagaparticipants.KitchenServiceProxy
import me.jangjunha.ftgo.order_service.sagaparticipants.OrderServiceProxy
import org.springframework.stereotype.Component

@Component
class CreateOrderSaga(
    accountingService: AccountingServiceProxy,
    consumerService: ConsumerServiceProxy,
    kitchenService: KitchenServiceProxy,
    orderService: OrderServiceProxy,
) : SimpleSaga<CreateOrderSagaState> {
    private val sagaDefinition: SagaDefinition<CreateOrderSagaState>

    init {
        this.sagaDefinition = this
            .step()
                .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
            .step()
                .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
            .step()
                .invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
                .onReply(CreateTicketReply::class.java, CreateOrderSagaState::handleCreateTicketReply)
                .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
//            TODO: 회계 서비스 구현 후 활성화시키기
//            .step()
//                .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
            .step()
                .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand)
            .build()
    }

    override fun getSagaDefinition(): SagaDefinition<CreateOrderSagaState> {
        return sagaDefinition
    }
}
