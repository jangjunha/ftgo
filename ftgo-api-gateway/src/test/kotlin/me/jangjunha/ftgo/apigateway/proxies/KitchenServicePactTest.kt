package me.jangjunha.ftgo.apigateway.proxies

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.BuilderUtils
import au.com.dius.pact.consumer.dsl.PactBuilder
import au.com.dius.pact.consumer.junit.MockServerConfig
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.consumer.model.MockServerImplementation
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Interaction
import au.com.dius.pact.core.model.annotations.Pact
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.common.auth.ExplicitCallCredentials
import me.jangjunha.ftgo.kitchen_service.api.Ticket
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(
    providerName = "ftgo-kitchen-service",
    providerType = ProviderType.SYNCH_MESSAGE,
    pactVersion = PactSpecVersion.V4
)
@MockServerConfig(implementation = MockServerImplementation.Plugin, registryEntry = "protobuf/transport/grpc")
class KitchenServicePactTest {

    @Pact(consumer = "ftgo-api-gateway")
    fun findTicketById_awaitingAcceptance(builder: PactBuilder) = builder
        .usingPlugin("protobuf")
        .given("ticket which state is `AWAITING_ACCEPTANCE`")
        .expectsToReceive("ticket details", "core/interaction/synchronous-message")
        .with(
            mapOf(
                Pair("pact:proto", BuilderUtils.filePath("../../ftgo-proto/protos/kitchens.proto")),
                Pair("pact:content-type", "application/grpc"),
                Pair("pact:proto-service", "KitchenService/GetTicket"),
                Pair(
                    "requestMetadata",
                    mapOf(
                        "x-ftgo-authenticated-restaurant-id" to "97e3c4c2-f336-4435-9314-ad1a633495df",
                    ),
                ),
                Pair(
                    "request",
                    mapOf(
                        Pair("ticketId", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                    ),
                ),
                Pair(
                    "response",
                    mapOf(
                        Pair("id", "matching(equalTo, '6f2d06a3-5dd2-4096-8644-6084d64eae35')"),
                        Pair("state", "matching(type, 'AWAITING_ACCEPTANCE')"),
                        Pair("restaurantId", "matching(equalTo, '97e3c4c2-f336-4435-9314-ad1a633495df')"),
                        Pair(
                            "lineItems",
                            listOf(
                                mapOf(
                                    Pair("quantity", "matching(number, 2)"),
                                    Pair("menuItemId", "matching(equalTo, 'latte')"),
                                    Pair("name", "matching(equalTo, 'Cafe Latte')"),
                                )
                            ),
                        ),
                    ),
                ),
            ),
        )
        .toPact()

    @Pact(consumer = "ftgo-api-gateway")
    fun findTicketById_accepted(builder: PactBuilder) = builder
        .usingPlugin("protobuf")
        .given("ticket which state is `ACCEPTED`")
        .expectsToReceive("ticket details", "core/interaction/synchronous-message")
        .with(
            mapOf(
                Pair("pact:proto", BuilderUtils.filePath("../../ftgo-proto/protos/kitchens.proto")),
                Pair("pact:content-type", "application/grpc"),
                Pair("pact:proto-service", "KitchenService/GetTicket"),
                Pair(
                    "requestMetadata",
                    mapOf(
                        "x-ftgo-authenticated-restaurant-id" to "97e3c4c2-f336-4435-9314-ad1a633495df",
                    ),
                ),
                Pair(
                    "request",
                    mapOf(
                        Pair("ticketId", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                    ),
                ),
                Pair(
                    "response",
                    mapOf(
                        Pair("id", "matching(equalTo, '6f2d06a3-5dd2-4096-8644-6084d64eae35')"),
                        Pair("state", "matching(type, 'ACCEPTED')"),
                        Pair("sequence", "matching(number, 101)"),
                        Pair("restaurantId", "matching(equalTo, '97e3c4c2-f336-4435-9314-ad1a633495df')"),
                        Pair(
                            "lineItems",
                            listOf(
                                mapOf(
                                    Pair("quantity", "matching(number, 2)"),
                                    Pair("menuItemId", "matching(equalTo, 'latte')"),
                                    Pair("name", "matching(equalTo, 'Cafe Latte')"),
                                )
                            ),
                        ),
                        Pair(
                            "readyBy", mapOf(
                                Pair("seconds", "matching(number, 1800)"),
                                Pair("nanos", "matching(number, 0)"),
                            )
                        ),
                        Pair(
                            "acceptTime", mapOf(
                                Pair("seconds", "matching(number, 0)"),
                                Pair("nanos", "matching(number, 0)"),
                            )
                        ),
                    ),
                ),
            ),
        )
        .toPact()

    @Pact(consumer = "ftgo-api-gateway")
    fun acceptTicket(builder: PactBuilder) = builder
        .usingPlugin("protobuf")
        .given("ticket which state is `AWAITING_ACCEPTANCE`")
        .expectsToReceive("successful response", "core/interaction/synchronous-message")
        .with(
            mapOf(
                Pair("pact:proto", BuilderUtils.filePath("../../ftgo-proto/protos/kitchens.proto")),
                Pair("pact:content-type", "application/grpc"),
                Pair("pact:proto-service", "KitchenService/AcceptTicket"),
                Pair(
                    "requestMetadata",
                    mapOf(
                        "x-ftgo-authenticated-restaurant-id" to "97e3c4c2-f336-4435-9314-ad1a633495df",
                    ),
                ),
                Pair(
                    "request",
                    mapOf(
                        Pair("ticketId", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                        Pair(
                            "readyBy", mapOf(
                                Pair("seconds", "1800"),
                                Pair("nanos", "0"),
                            )
                        ),
                    ),
                ),
                Pair("response", emptyMap<Unit, Unit>()),
            ),
        )
        .toPact()

    @Test
    @PactTestFor(pactMethod = "findTicketById_awaitingAcceptance")
    fun testFindTicketById_awaitingAcceptance(mockServer: MockServer, interaction: V4Interaction.SynchronousMessages) =
        runBlocking {
            val service = KitchenService(
                Destinations(
                    kitchenServiceUrl = "127.0.0.1:${mockServer.getPort()}",
                    orderServiceUrl = "",
                    orderHistoryServiceUrl = "",
                    restaurantServiceUrl = "",
                )
            )
            val expected = Ticket.parseFrom(interaction.response[0].contents.value)
            val ticket =
                service.findTicketById(
                    UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                    ExplicitCallCredentials(
                        "x-ftgo-authenticated-restaurant-id",
                        "97e3c4c2-f336-4435-9314-ad1a633495df"
                    )
                )
            assert(expected == ticket)
        }

    @Test
    @PactTestFor(pactMethod = "findTicketById_accepted")
    fun testFindTicketById_accepted(mockServer: MockServer, interaction: V4Interaction.SynchronousMessages) =
        runBlocking {
            val service = KitchenService(
                Destinations(
                    kitchenServiceUrl = "127.0.0.1:${mockServer.getPort()}",
                    orderServiceUrl = "",
                    orderHistoryServiceUrl = "",
                    restaurantServiceUrl = "",
                )
            )
            val expected = Ticket.parseFrom(interaction.response[0].contents.value)
            val ticket =
                service.findTicketById(
                    UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                    ExplicitCallCredentials(
                        "x-ftgo-authenticated-restaurant-id",
                        "97e3c4c2-f336-4435-9314-ad1a633495df"
                    )
                )
            assert(expected == ticket)
        }

    @Test
    @PactTestFor(pactMethod = "acceptTicket")
    fun testAcceptTicket(mockServer: MockServer) =
        runBlocking {
            val service = KitchenService(
                Destinations(
                    kitchenServiceUrl = "127.0.0.1:${mockServer.getPort()}",
                    orderServiceUrl = "",
                    orderHistoryServiceUrl = "",
                    restaurantServiceUrl = "",
                )
            )
            assertDoesNotThrow {
                service.acceptTicket(
                    UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                    OffsetDateTime.parse("1970-01-01T00:30Z"),
                    ExplicitCallCredentials(
                        "x-ftgo-authenticated-restaurant-id",
                        "97e3c4c2-f336-4435-9314-ad1a633495df"
                    )
                )
            }
        }
}
