package me.jangjunha.ftgo.eventuate.tram.testutil

import io.eventuate.tram.messaging.common.Message

interface MessageReceiver {

    fun receive(destination: String): Message
}
