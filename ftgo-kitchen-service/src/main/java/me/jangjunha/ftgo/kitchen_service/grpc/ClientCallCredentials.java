package me.jangjunha.ftgo.kitchen_service.grpc;

import me.jangjunha.ftgo.common.auth.ExplicitCallCredentials;

public class ClientCallCredentials extends ExplicitCallCredentials {

    public ClientCallCredentials() {
        super("x-ftgo-authenticated-client-id", "ftgo-kitchen-service");
    }
}
