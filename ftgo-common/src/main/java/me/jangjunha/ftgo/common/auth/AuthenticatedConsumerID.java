package me.jangjunha.ftgo.common.auth;

import java.util.UUID;

public final class AuthenticatedConsumerID extends AuthenticatedID {

    private final UUID consumerId;

    public AuthenticatedConsumerID(UUID consumerId) {
        this.consumerId = consumerId;
    }

    public UUID getConsumerId() {
        return consumerId;
    }
}
