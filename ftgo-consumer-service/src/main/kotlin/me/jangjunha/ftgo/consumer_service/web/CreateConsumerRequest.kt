package me.jangjunha.ftgo.consumer_service.web

data class CreateConsumerRequest(
    val name: String,
) {
    protected constructor() : this("")
}
