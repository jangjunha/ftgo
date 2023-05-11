package me.jangjunha.ftgo.order_service.api

enum class OrderState {
    APPROVAL_PENDING,
    APPROVED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED,
    REVISION_PENDING,
}