package me.jangjunha.ftgo.apigateway.proxies

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PM
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslRootValue
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.MockServerConfig
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.restaurant_service.api.MenuItem
import me.jangjunha.ftgo.restaurant_service.api.Restaurant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "ftgo-restaurant-service", pactVersion = PactSpecVersion.V4)
@MockServerConfig
class RestaurantServicePactTest {

    @Pact(consumer = "ftgo-api-gateway")
    fun findRestaurantById(builder: PactDslWithProvider): V4Pact = builder
        .given("'A Cafe' restaurant exist")
        .uponReceiving("retrieving restaurant data")
            .path("/restaurants/97e3c4c2-f336-4435-9314-ad1a633495df/")
            .method("GET")
        .willRespondWith()
            .status(200)
            .headers(
                mapOf(
                    Pair("Content-Type", "application/json"),
                )
            )
            .body(
                PactDslJsonBody()
                    .uuid("id", "97e3c4c2-f336-4435-9314-ad1a633495df")
                    .stringType("name", "A Cafe")
                    .eachLike("menuItems")
                        .stringType("id", "americano")
                        .stringType("name", "Americano")
                        .`object`("price")
                            .or("amount", 2500, PM.stringType(), PM.decimalType())
                            .closeObject()!!
                        .closeObject()!!
                        .closeArray()!!
            )
        .toPact(V4Pact::class.java)

    @Test
    @PactTestFor(pactMethod = "findRestaurantById")
    fun testFindRestaurantById(mockServer: MockServer) = runBlocking {
        val svc = RestaurantService(Destinations(
            restaurantServiceUrl = "${mockServer.getUrl()}/",
            orderServiceUrl = "",
            orderHistoryServiceUrl = "",
            kitchenServiceUrl = "",
        ), WebClient.create())
        val restaurant = svc.findRestaurantById(UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"))
        assert(
            restaurant == Restaurant(
                UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                "A Cafe",
                listOf(
                    MenuItem("americano", "Americano", Money("2500")),
                )
            )
        )
    }
}
