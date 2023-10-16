package me.jangjunha.ftgo.common.auth;

public sealed class AuthenticatedID permits AuthenticatedClient, AuthenticatedConsumerID, AuthenticatedRestaurantID, AuthenticatedCourierID {
}
