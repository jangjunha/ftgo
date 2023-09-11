package me.jangjunha.ftgo.apigateway.contracts

import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.apigateway.Destinations
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = [
        "destinations.orderServiceUrl=http://localhost:6565/",
        "destinations.orderHistoryServiceUrl=http://localhost:9999/",
        "destinations.kitchenServiceUrl=http://localhost:6566/",
        "destinations.restaurantServiceUrl=http://localhost:9999/",
    ]
)
@AutoConfigureStubRunner(ids = [
    "me.jangjunha.ftgo:ftgo-order-service:+:stubs:6565",
    "me.jangjunha.ftgo:ftgo-kitchen-service:+:stubs:6566",
], stubsMode = StubsMode.LOCAL)
//@AutoConfigureWireMock
//@DirtiesContext
class OrderServiceTests {

    @LocalServerPort
    val port: Int = 0

    @Value("\${stubrunner.runningstubs.ftgo-order-service.port}")
    val orderServicePort = 0

    @Test
    fun shouldProxyCreateOrder() = runBlocking {
        println(port)
        val client = WebClient.create("http://localhost:${port}/")
        val result: Map<String, Any> =
            client.post().uri("/orders/")
                .header("Content-Type", "application/json").header("Accept", "application/json")
                .bodyValue(
                    """
            {
                "restaurantId": "2a048668-4b29-456c-a798-6b2c4f6ba973",
                "consumerId": "bbd1ae72-3323-4213-9f00-04df6fc4900f",
                "items": [
                    {"menuItemId": "latte", "quantity": 2}
                ],
                "deliveryAddress": "서울시 강남구 테헤란로 1"
            }
        """.trimIndent()
                ).awaitExchange { response ->
                    assert(response.statusCode() == HttpStatus.OK) {
                        "Expects OK, but actual ${response.statusCode()}"
                    }
                    response.awaitBody()
                }
        val orderId = result["orderId"] as? String
        assert(orderId != null)
        assertDoesNotThrow {
            UUID.fromString(orderId)
        }
        Unit
    }

//    @Configuration
//    @EnableAutoConfiguration
//    class Config {
//        @Bean
//        fun webServerFactory() = NettyReactiveWebServerFactory()
//    }
}
