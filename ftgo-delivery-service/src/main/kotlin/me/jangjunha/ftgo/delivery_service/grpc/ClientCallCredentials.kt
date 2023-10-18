package me.jangjunha.ftgo.delivery_service.grpc;

import me.jangjunha.ftgo.common.auth.ExplicitCallCredentials;

class ClientCallCredentials: ExplicitCallCredentials("x-ftgo-authenticated-client-id", "ftgo-kitchen-service")
