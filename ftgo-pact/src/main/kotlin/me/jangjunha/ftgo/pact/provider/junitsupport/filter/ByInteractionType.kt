package me.jangjunha.ftgo.pact.provider.junitsupport.filter

import au.com.dius.pact.core.model.Interaction
import au.com.dius.pact.core.model.SynchronousRequestResponse
import au.com.dius.pact.core.model.V4Interaction
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.provider.junitsupport.filter.InteractionFilter
import java.util.function.Predicate

class ByInteractionType<I : Interaction> : InteractionFilter<I> {
    override fun buildPredicate(values: Array<String>): Predicate<I> {
        return Predicate { interaction: I ->
            values.any { value ->
                when (value) {
                    "Http" -> interaction is SynchronousRequestResponse
                    "V4Http" -> interaction is V4Interaction.SynchronousHttp
                    "GRPC" -> interaction is V4Interaction.SynchronousMessages && interaction.transport == "grpc"
                    "Message" -> interaction is Message
                    else -> false
                }
            }
        }
    }
}
