package me.jangjunha.ftgo.order_service.domain

data class MenuItemIdAndQuantity(
    val menuItemId: String,
    val quantity: Int,
) {
    protected constructor(): this("", 0)
}
