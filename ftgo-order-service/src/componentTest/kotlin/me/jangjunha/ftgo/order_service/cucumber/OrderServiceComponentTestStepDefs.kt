package me.jangjunha.ftgo.order_service.cucumber

import io.cucumber.java.ko.그러면
import io.cucumber.java.ko.만약
import io.cucumber.java.ko.조건
import io.cucumber.spring.CucumberContextConfiguration
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess
import io.eventuate.tram.events.publisher.DomainEventPublisher
import io.eventuate.tram.messaging.consumer.MessageConsumer
import io.eventuate.tram.sagas.spring.testing.SagaParticipantStubManagerConfiguration
import io.eventuate.tram.sagas.testing.SagaParticipantChannels
import io.eventuate.tram.sagas.testing.SagaParticipantStubManager
import io.eventuate.tram.testing.MessageTracker
import io.eventuate.util.test.async.Eventually.eventually
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import me.jangjunha.ftgo.consumer_service.api.ConsumerServiceChannels
import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer
import me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.api.createOrderPayload
import me.jangjunha.ftgo.order_service.api.events.OrderRejected
import me.jangjunha.ftgo.order_service.api.getOrderPayload
import me.jangjunha.ftgo.order_service.api.menuItemIdAndQuantity
import me.jangjunha.ftgo.order_service.domain.RestaurantRepository
import me.jangjunha.ftgo.order_service.grpc.OrderServiceImpl
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.order_service.service.OrderServiceConfiguration
import me.jangjunha.ftgo.order_service.service.OrderServiceMessagingConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotNull


//@ExtendWith(SpringExtension::class)
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = ["grpc.server.port=0"])
class OrderServiceComponentTestStepDefs
@Autowired constructor(
    private val domainEventPublisher: DomainEventPublisher,
    private val messageTracker: MessageTracker,
    private val sagaParticipantStubManager: SagaParticipantStubManager,
    private val restaurantRepository: RestaurantRepository,
    private val orderService: OrderService,
) {
    private val orderController = OrderServiceImpl(orderService)

    private var order: Order? = null

    @BeforeEach
    fun setUp() {
        sagaParticipantStubManager.reset()
    }

    @조건("내가 유효한 소비자라면")
    fun 내가_유효한_소비자라면() {
        sagaParticipantStubManager
            .forChannel(ConsumerServiceChannels.consumerServiceChannel)
            .`when`(ValidateOrderByConsumer::class.java)
            .replyWithSuccess()
    }

    @조건("내 통장에 잔액이 없다면")
    fun 내_통장에_잔액이_없다면() {
        sagaParticipantStubManager
            .forChannel(AccountingServiceChannels.accountingServiceChannel)
            .`when`(WithdrawCommand::class.java)
            .replyWithFailure()
    }

    @조건("A Cafe 매장이 주문을 받는다면")
    fun a_cafe_매장이_주문을_받는다면() {
        sagaParticipantStubManager
            .forChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .`when`(CreateTicket::class.java)
            .replyWith { withSuccess(CreateTicketReply(it.command.orderId, 101L)) }
            .`when`(ConfirmCreateTicket::class.java)
            .replyWithSuccess()
            .`when`(CancelCreateTicket::class.java)
            .replyWithSuccess()

        if (!restaurantRepository.existsById(Fixtures.A_CAFE.id)) {
            domainEventPublisher.publish(
                "me.jangjunha.ftgo.restaurant_service.domain.Restaurant",
                Fixtures.A_CAFE.id,
                listOf(Fixtures.makeACafeCreatedEvent())
            )
            eventually {
                assert(restaurantRepository.existsById(Fixtures.A_CAFE.id))
            }
        }
    }

    @만약("내가 이 음식점에 Americano를 주문할 경우")
    fun 내가_이_음식점에_americano를_주문할_경우() = runBlocking {
        order = orderController.createOrder(createOrderPayload {
            restaurantId = Fixtures.A_CAFE.id.toString()
            consumerId = Fixtures.CONSUMER_ID.toString()
            deliveryAddress = "서울시 강남구 테헤란로 1"
            items.addAll(listOf(
                menuItemIdAndQuantity {
                    menuItemId = "americano"
                    quantity = 1
                }
            ))
        })
    }

    @그러면("나의 주문은 거절되어야 한다.")
    fun 나의_주문은_거절되어야_한다() = runBlocking {
        // TODO:
        eventually {
            runBlocking {
                assertNotNull(order) {
                    val res = orderController.getOrder(getOrderPayload { id = it.id.toString() })
                    assert(res.state == OrderState.REJECTED)
                }
            }
        }
    }

    @그러면("OrderRejected 이벤트가 발행되어야 한다.")
    fun order_rejected_이벤트가_발행되어야_한다() {
        messageTracker.assertDomainEventPublished(
            "me.jangjunha.ftgo.order_service.domain.Order",
            OrderRejected::class.qualifiedName
        )
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(
        SagaParticipantStubManagerConfiguration::class,
    )
    @ComponentScan("me.jangjunha.ftgo.order_service")
//    @EnableJpaRepositories(basePackageClasses = [RestaurantRepository::class])
//    @EntityScan(basePackageClasses = [me.jangjunha.ftgo.order_service.domain.Order::class])
//    @ComponentScan(basePackageClasses = [OrderService::class])
    class TestConfiguration {

        @Bean
        fun sagaParticipantChannels() =
            SagaParticipantChannels("consumerService", "kitchenService", "accountingService", "orderService")

        @Bean
        fun messageTracker(messageConsumer: MessageConsumer) =
            MessageTracker(setOf("me.jangjunha.ftgo.order_service.domain.Order"), messageConsumer)

//        @Bean
//        fun orderServiceImpl(orderService: OrderService) = OrderServiceImpl(orderService)
    }
}
