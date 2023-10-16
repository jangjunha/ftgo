package me.jangjunha.ftgo.common.auth;

import java.util.UUID;

public final class AuthenticatedCourierID extends AuthenticatedID {

    private final UUID courierId;

    public AuthenticatedCourierID(UUID courierId) {
        this.courierId = courierId;
    }

    public UUID getCourierId() {
        return courierId;
    }
}
