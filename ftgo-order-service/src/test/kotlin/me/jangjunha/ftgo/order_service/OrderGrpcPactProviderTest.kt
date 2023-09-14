package me.jangjunha.ftgo.order_service

import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junit5.PluginTestTarget
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFilter
import io.mockk.every
import io.mockk.mockk
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.Order
import me.jangjunha.ftgo.order_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_service.domain.OrderLineItems
import me.jangjunha.ftgo.order_service.grpc.GrpcServer
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.time.OffsetDateTime
import java.util.*


@Provider("ftgo-order-service")
@PactFilter(value = ["GRPC"], filter = ByInteractionType::class)
@PactBroker(url = "https://pact.ftgo.jangjunha.me/")
class OrderGrpcPactProviderTest {

    private val port = 50011

    private val orderService = mockk<OrderService>()

    private val grpcServer = GrpcServer(port, orderService)

    @State("an order")
    fun toOrderExists() {
        every { orderService.getOrder(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35")) } returns Order(
            id = UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            state = OrderState.APPROVAL_PENDING,
            consumerId = UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad"),
            restaurantId = UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
            orderLineItems = OrderLineItems(mutableListOf(
                OrderLineItem(2, "latte", "Cafe Latte", Money("3500")),
            )),
            deliveryInformation = DeliveryInformation(
                deliveryTime = OffsetDateTime.MIN,
                deliveryAddress = "서울시 강남구 테헤란로 1",
            ),
        )
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTeamplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        grpcServer.start()
        context.target = PluginTestTarget(mutableMapOf(
            Pair("host", "localhost"),
            Pair("port", port),
            Pair("transport", "grpc"),
        ))
    }

    @AfterEach
    fun tearDown() {
        grpcServer.stop()
    }
}
