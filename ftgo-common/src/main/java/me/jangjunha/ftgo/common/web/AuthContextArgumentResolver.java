package me.jangjunha.ftgo.common.web;

import jakarta.servlet.http.HttpServletRequest;
import me.jangjunha.ftgo.common.auth.AuthenticatedClient;
import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID;
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

public class AuthContextArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String HEADER_CLIENT_ID = "x-ftgo-authenticated-client-id";
    private final static String HEADER_CONSUMER_ID = "x-ftgo-authenticated-consumer-id";
    private final static String HEADER_RESTAURANT_ID = "x-ftgo-authenticated-restaurant-id";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthContext.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String clientId = request.getHeader(HEADER_CLIENT_ID);
        String consumerId = request.getHeader(HEADER_CONSUMER_ID);
        String restaurantId = request.getHeader(HEADER_RESTAURANT_ID);
        if (clientId != null) {
            return new AuthenticatedClient(clientId);
        }
        if (consumerId != null) {
            return new AuthenticatedConsumerID(UUID.fromString(consumerId));
        }
        if (restaurantId != null) {
            return new AuthenticatedRestaurantID(UUID.fromString(restaurantId));
        }
        return null;
    }
}
