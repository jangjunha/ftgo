package me.jangjunha.ftgo.apigateway.routers.orders

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit.MockServerConfig
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import org.apache.commons.io.IOUtils
import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.ContentType
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONParser

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "ftgo-order-service", pactVersion = PactSpecVersion.V4)
@MockServerConfig
class OrderServicePactTest {

    @Pact(consumer = "ftgo-api-gateway")
    fun createOrder(builder: PactBuilder): V4Pact = builder
        .given("a restaurant and a consumer")
        .expectsToReceiveHttpInteraction("creating order") { httpBuilder ->
            httpBuilder
                .withRequest { request ->
                    request
                        .method("POST")
                        .path("/orders/")
                        .body(
                            PactDslJsonBody()
                                .uuid("restaurantId", "97e3c4c2-f336-4435-9314-ad1a633495df")
                                .uuid("consumerId", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
                                .stringType("deliveryAddress", "서울시 강남구 테헤란로 1")
                                .eachLike("items")
                                .stringType("menuItemId", "americano")
                                .numberType("quantity", 2)
                                .closeObject()!!
                                .closeArray()!!
                        )
                }
                .willRespondWith { response ->
                    response.body(
                        PactDslJsonBody()
                            .uuid("orderId", "6f2d06a3-5dd2-4096-8644-6084d64eae35")
                    )
                }
        }
        .toPact()

    @Test
    @PactTestFor(pactMethod = "createOrder")
    fun testCreateOrder(mockServer: MockServer) {
        val response = Request.post("${mockServer.getUrl()}/orders/").bodyString(
            """
            {
                "restaurantId": "97e3c4c2-f336-4435-9314-ad1a633495df",
                "consumerId": "627a9a8a-41af-4daf-a968-00ffc80b53ad",
                "deliveryAddress": "서울시 강남구 테헤란로 1",
                "items": [
                    {"menuItemId": "americano", "quantity": 2}
                ]
            }                
            """.trimIndent(), ContentType.APPLICATION_JSON
        ).execute().returnResponse() as ClassicHttpResponse
        val result = JSONParser.parseJSON(IOUtils.toString(response.entity.content, "utf-8")) as JSONObject

        assert(response.code == 200)
        JSONAssert.assertEquals(
            """
            {
                "orderId": "6f2d06a3-5dd2-4096-8644-6084d64eae35"
            }
        """.trimIndent(), result, false
        )
    }
}
