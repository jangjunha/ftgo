package me.jangjunha.ftgo.eventuate.tram.testutil

import io.eventuate.common.id.IdGenerator
import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.tram.commands.common.Command
import io.eventuate.tram.commands.consumer.CommandWithDestination
import io.eventuate.tram.sagas.orchestration.CommandWithDestinationAndType
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint

class SagaMessagingTestHelper(
    private val messageReceiver: MessageReceiver,
    private val sagaCommandProducer: SagaCommandProducer,
    private val idGenerator: IdGenerator,
) {

    fun <C : Command> sendCommand(
        commandEndpoint: CommandEndpoint<C>,
        command: C,
        sagaType: String
    ) {
        val sagaId = idGenerator.genId().asString()

        val replyTo = "$sagaType-reply"
        sagaCommandProducer.sendCommands(
            sagaType,
            sagaId,
            mutableListOf(
                CommandWithDestinationAndType.command(
                    CommandWithDestination(
                        commandEndpoint.commandChannel,
                        null,
                        command
                    )
                )
            ),
            replyTo
        )
    }

    fun <C : Command, R : Any> sendAndReceiveCommand(
        commandEndpoint: CommandEndpoint<C>,
        command: C,
        replyClass: Class<R>,
        sagaType: String
    ): R {
        sendCommand(commandEndpoint, command, sagaType)

        val replyTo = "$sagaType-reply"
        val response = messageReceiver.receive(replyTo)
        return JSonMapper.fromJson(response.payload, replyClass)
    }
}
