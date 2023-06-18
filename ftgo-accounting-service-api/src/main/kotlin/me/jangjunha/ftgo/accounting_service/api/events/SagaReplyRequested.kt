package me.jangjunha.ftgo.accounting_service.api.events


// TODO: success / fail
data class SagaReplyRequested(
    val correlationHeaders: Map<String, String> = emptyMap(),
): AccountEvent()
