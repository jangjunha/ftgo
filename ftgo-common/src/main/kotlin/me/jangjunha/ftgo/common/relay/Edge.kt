package me.jangjunha.ftgo.common.relay

data class Edge<T>(
    val node: T,
    val cursor: String,
)
