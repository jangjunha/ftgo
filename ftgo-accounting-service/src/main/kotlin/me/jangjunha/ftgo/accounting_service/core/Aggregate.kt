package me.jangjunha.ftgo.accounting_service.core

interface Aggregate<ID, E> {
    val id: ID

    fun apply(event: E)
}
