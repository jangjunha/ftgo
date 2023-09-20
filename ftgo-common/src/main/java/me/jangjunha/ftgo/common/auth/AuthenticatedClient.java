package me.jangjunha.ftgo.common.auth;

public final class AuthenticatedClient extends AuthenticatedID {

    private final String clientId;

    public AuthenticatedClient(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
