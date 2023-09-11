package me.jangjunha.ftgo.apigateway.routers.orders

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit.MockServerConfig
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.ClassicHttpResponse
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONParser
import java.time.Instant
import java.util.*


@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "ftgo-order-history-service", pactVersion = PactSpecVersion.V4)
@MockServerConfig
class OrderHistoryServicePactTest {

    @Pact(consumer = "ftgo-api-gateway")
    fun getOrdersByConsumer(builder: PactDslWithProvider): V4Pact = builder
        .given("number of orders for a consumer")
        .uponReceiving("list of order details")
            .path("/orders/")
            .matchQuery("consumerId", "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\$", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
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
                    .stringType("startKey", "NEXT-TOKEN")
                    .eachLike("orders")
                        .uuid("orderId", "6f2d06a3-5dd2-4096-8644-6084d64eae35")
                        .stringType("status", "APPROVAL_PENDING")
                        .uuid("restaurantId", "97e3c4c2-f336-4435-9314-ad1a633495df")
                        .stringType("restaurantName", "A Cafe")
                        .uuid("consumerId", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
                        .datetime("creationDate", "yyyy-MM-dd'T'HH[':'mm[':'ss['.'SSS]]]X", Date.from(Instant.ofEpochSecond(0)), TimeZone.getTimeZone("Asia/Seoul"))
                        .eachLike("lineItems")
                            .numberType("quantity", 2)
                            .stringType("menuItemId", "americano")
                            .stringType("name", "Americano")
                            .`object`("price")
                                .numberType("amount", 2500)
                                .closeObject()!!
                            .closeObject()!!
                            .closeArray()!!
                    .closeObject()!!
                    .closeArray()!!
            )
        .toPact(V4Pact::class.java)

    @Test
    @PactTestFor(pactMethod = "getOrdersByConsumer")
    fun getOrdersByConsumer(mockServer: MockServer) {
        val response = Request.get("${mockServer.getUrl()}/orders/?consumerId=627a9a8a-41af-4daf-a968-00ffc80b53ad").execute().returnResponse() as ClassicHttpResponse
        val order = JSONParser.parseJSON(IOUtils.toString(response.entity.content, "utf-8")) as JSONObject

        assert(response.code == 200)
        assertEquals("""
            {
                "orders": [
                    {
                        "orderId": "6f2d06a3-5dd2-4096-8644-6084d64eae35",
                        "status": "APPROVAL_PENDING",
                        "restaurantId": "97e3c4c2-f336-4435-9314-ad1a633495df",
                        "restaurantName": "A Cafe",
                        "consumerId": "627a9a8a-41af-4daf-a968-00ffc80b53ad",
                        "creationDate": "1970-01-01T09:00:00.000+09",
                        "lineItems": [
                            {
                                "quantity": 2,
                                "menuItemId": "americano",
                                "name": "Americano",
                                "price": {"amount": 2500}
                            }
                        ]
                    }
                ],
                "startKey": "NEXT-TOKEN"
            }
        """.trimIndent(), order, false)
    }
}
