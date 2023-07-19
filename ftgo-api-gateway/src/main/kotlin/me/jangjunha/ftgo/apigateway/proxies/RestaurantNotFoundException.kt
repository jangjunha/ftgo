package me.jangjunha.ftgo.apigateway.proxies

import java.util.UUID

class RestaurantNotFoundException(id: UUID) : RuntimeException("Cannot find restaurant $id")
