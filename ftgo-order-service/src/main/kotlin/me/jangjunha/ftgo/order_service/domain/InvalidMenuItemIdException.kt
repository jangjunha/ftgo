package me.jangjunha.ftgo.order_service.domain

data class InvalidMenuItemIdException (
    val menuItemId: String,
): RuntimeException(String.format("Cannot find menuItem with id %s", menuItemId))
