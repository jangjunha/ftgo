package me.jangjunha.ftgo.apigateway.proxies

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.BuilderUtils.filePath
import au.com.dius.pact.consumer.dsl.PactBuilder
import au.com.dius.pact.consumer.junit.MockServerConfig
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.consumer.model.MockServerImplementation
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Interaction
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.order_service.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "ftgo-order-service", providerType = ProviderType.SYNCH_MESSAGE, pactVersion = PactSpecVersion.V4)
@MockServerConfig(implementation = MockServerImplementation.Plugin, registryEntry = "protobuf/transport/grpc")
class OrderServicePactTest {

    @Pact(consumer = "ftgo-api-gateway")
    fun findOrderById(builder: PactBuilder): V4Pact = builder
        .usingPlugin("protobuf")
        .given("an order")
        .expectsToReceive("order details", "core/interaction/synchronous-message")
        .with(mapOf(
            Pair("pact:proto", filePath("../../ftgo-proto/protos/orders.proto")),
            Pair("pact:content-type", "application/grpc"),
            Pair("pact:proto-service", "OrderService/getOrder"),
            Pair("request", mapOf(
                Pair("id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            )),
            Pair("response", mapOf(
                Pair("id", "matching(equalTo, '6f2d06a3-5dd2-4096-8644-6084d64eae35')"),
                Pair("state", "matching(type, 'APPROVAL_PENDING')"),
                Pair("consumerId", "matching(equalTo, '627a9a8a-41af-4daf-a968-00ffc80b53ad')"),
                Pair("restaurantId", "matching(equalTo, '97e3c4c2-f336-4435-9314-ad1a633495df')"),
                Pair("lineItems", listOf(
                    mapOf(
                        Pair("quantity", "matching(number, 2)"),
                        Pair("menuItemId", "matching(equalTo, 'latte')"),
                        Pair("name", "matching(equalTo, 'Cafe Latte')"),
                        Pair("price", mapOf(
                            Pair("amount", "matching(number, 3500)")
                        )),
                    ),
                )),
                Pair("deliveryInformation", mapOf(
                    Pair("deliveryTime", mapOf(
                        Pair("seconds", "matching(number, 0)"),
                        Pair("nanos", "matching(number, 0)"),
                    )),
                    Pair("deliveryAddress", "matching(equalTo, '서울시 강남구 테헤란로 1')")
                )),
                Pair("orderMinimum", mapOf(
                    Pair("amount", "matching(number, 2147483647)")
                )),
            )),
        ))
        .toPact()

    @Test
    @PactTestFor(pactMethod = "findOrderById")
    fun testOrderById(mockServer: MockServer, interaction: V4Interaction.SynchronousMessages) = runBlocking {
        val service = OrderService(Destinations(
            orderServiceUrl = "127.0.0.1:${mockServer.getPort()}",
            orderHistoryServiceUrl = "",
            kitchenServiceUrl = "",
            restaurantServiceUrl = "",
        ))
        val expected = Order.parseFrom(interaction.response[0].contents.value)
        val order = service.findOrderById(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"))
        assert(expected == order)
    }
}
