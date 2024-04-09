package me.jangjunha.ftgo.apigateway

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFilter
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import me.jangjunha.ftgo.apigateway.proxies.RestaurantService
import me.jangjunha.ftgo.apigateway.routers.restaurants.RestaurantConfiguration
import me.jangjunha.ftgo.apigateway.routers.restaurants.RestaurantHandlers
import me.jangjunha.ftgo.apigateway.security.SecurityConfig
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType
import me.jangjunha.ftgo.restaurant_service.api.MenuItem
import me.jangjunha.ftgo.restaurant_service.api.Restaurant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("ftgo-api-gateway")
@PactFilter(value = ["Http", "V4Http"], filter = ByInteractionType::class)
@PactBroker
class HttpPactProviderTest {

    @LocalServerPort
    var port = 0

    @MockkBean
    lateinit var restaurantService: RestaurantService

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("I have a list of restaurants")
    fun toHaveRestaurants() {
        coEvery { restaurantService.listRestaurants() } returns listOf(
            Restaurant(UUID(0, 0), "A Cafe", listOf(
                MenuItem("americano", "Americano", Money("1500")),
                MenuItem("latte", "Cafe Latte", Money("2500")),
            )),
            Restaurant(UUID(0, 1), "Foo Coffee", listOf(
                MenuItem("americano", "Americano", Money("1900")),
            ))
        )
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(value = [
        APIGatewayConfiguration::class,
        SecurityConfig::class,
        RestaurantConfiguration::class,
    ])
    class TestConfig {
        @Bean
        fun restaurantHandlers(restaurantService: RestaurantService) = RestaurantHandlers(restaurantService)
    }
}
