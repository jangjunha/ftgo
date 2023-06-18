package me.jangjunha.ftgo.accounting_service.api.events


class SagaReplyRequested(
    val correlationHeaders: Map<String, String> = emptyMap(),
    val status: SagaReplyStatus = SagaReplyStatus.SUCCESS,
    val reply: Any? = null
): AccountEvent() {

    enum class SagaReplyStatus {
        SUCCESS,
        FAILURE,
    }
}
