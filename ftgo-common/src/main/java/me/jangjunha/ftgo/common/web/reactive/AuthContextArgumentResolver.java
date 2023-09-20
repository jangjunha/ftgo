package me.jangjunha.ftgo.common.web.reactive;

import me.jangjunha.ftgo.common.auth.AuthenticatedClient;
import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID;
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID;
import me.jangjunha.ftgo.common.web.AuthContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class AuthContextArgumentResolver implements HandlerMethodArgumentResolver {
    private final static String HEADER_CLIENT_ID = "x-ftgo-authenticated-client-id";
    private final static String HEADER_CONSUMER_ID = "x-ftgo-authenticated-consumer-id";
    private final static String HEADER_RESTAURANT_ID = "x-ftgo-authenticated-restaurant-id";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthContext.class) != null;
    }

    @NotNull
    @Override
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter, @NotNull BindingContext bindingContext, ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String clientId = headers.getFirst(HEADER_CLIENT_ID);
        String consumerId = headers.getFirst(HEADER_CONSUMER_ID);
        String restaurantId = headers.getFirst(HEADER_RESTAURANT_ID);
        if (clientId != null) {
            return Mono.just(new AuthenticatedClient(clientId));
        }
        if (consumerId != null) {
            return Mono.just(new AuthenticatedConsumerID(UUID.fromString(consumerId)));
        }
        if (restaurantId != null) {
            return Mono.just(new AuthenticatedRestaurantID(UUID.fromString(restaurantId)));
        }
        return Mono.empty();
    }
}
