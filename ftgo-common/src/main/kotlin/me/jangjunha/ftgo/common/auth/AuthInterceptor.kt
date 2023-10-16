package me.jangjunha.ftgo.common.auth

import io.grpc.*

import java.util.UUID;

class AuthInterceptor : ServerInterceptor {

    companion object {
        @JvmStatic
        val AUTHENTICATED_ID: Context.Key<AuthenticatedID?> = Context.key("autheneticated_id")

        private val HEADER_CLIENT_ID: Metadata.Key<String> =
            Metadata.Key.of("x-ftgo-authenticated-client-id", Metadata.ASCII_STRING_MARSHALLER)
        private val HEADER_CONSUMER_ID: Metadata.Key<String> =
            Metadata.Key.of("x-ftgo-authenticated-consumer-id", Metadata.ASCII_STRING_MARSHALLER)
        private val HEADER_RESTAURANT_ID: Metadata.Key<String> =
            Metadata.Key.of("x-ftgo-authenticated-restaurant-id", Metadata.ASCII_STRING_MARSHALLER)
        private val HEADER_COURIER_ID: Metadata.Key<String> =
            Metadata.Key.of("x-ftgo-authenticated-courier-id", Metadata.ASCII_STRING_MARSHALLER)
    }

    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val clientId = headers.get(HEADER_CLIENT_ID)
        val consumerId = headers.get(HEADER_CONSUMER_ID)
        val restaurantId = headers.get(HEADER_RESTAURANT_ID)
        val courierId = headers.get(HEADER_COURIER_ID)
        val context = if (clientId != null) {
            Context.current().withValue(AUTHENTICATED_ID, AuthenticatedClient(clientId))
        } else if (consumerId != null) {
            Context.current().withValue(AUTHENTICATED_ID, AuthenticatedConsumerID(UUID.fromString(consumerId)))
        } else if (restaurantId != null) {
            Context.current().withValue(AUTHENTICATED_ID, AuthenticatedRestaurantID(UUID.fromString(restaurantId)))
        } else if (courierId != null) {
            Context.current().withValue(AUTHENTICATED_ID, AuthenticatedCourierID(UUID.fromString(courierId)))
        } else {
            Context.current()
        }
        return Contexts.interceptCall(context, call, headers, next);
    }
}
