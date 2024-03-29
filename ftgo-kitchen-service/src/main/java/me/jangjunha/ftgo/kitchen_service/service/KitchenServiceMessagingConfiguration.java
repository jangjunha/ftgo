package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration;
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration;
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TramEventsPublisherConfiguration.class,
        TramEventSubscriberConfiguration.class,
        // configure default ChannelMapping bean
        TramMessagingCommonAutoConfiguration.class,
        // configure default DomainEventNameMapping bean
        TramEventsCommonAutoConfiguration.class,
        // configure SagaCommandDispatcherFactory
        SagaParticipantConfiguration.class,
        // configure default CommandNameMapping
        TramCommandsCommonAutoConfiguration.class,
})
public class KitchenServiceMessagingConfiguration {
    @Bean
    public DomainEventDispatcher domainEventDispatcher(
            KitchenServiceEventConsumer kitchenServiceEventConsumer,
            DomainEventDispatcherFactory domainEventDispatcherFactory
    ) {
        return domainEventDispatcherFactory.make("kitchenServiceEvents", kitchenServiceEventConsumer.domainEventHandlers());
    }

    @Bean
    @Autowired
    public CommandDispatcher commandDispatcher(
            KitchenServiceCommandHandler kitchenServiceCommandHandler,
            SagaCommandDispatcherFactory sagaCommandDispatcherFactory
    ) {
        return sagaCommandDispatcherFactory.make(
                "kitchenServiceCommandDispatcher",
                kitchenServiceCommandHandler.commandHandlers()
        );
    }

    @Bean
    public KitchenServiceEventConsumer kitchenServiceEventConsumer(
            KitchenService kitchenService
    ) {
        return new KitchenServiceEventConsumer(kitchenService);
    }

    @Bean
    public KitchenServiceCommandHandler kitchenServiceCommandHandler(
            KitchenService kitchenService
    ) {
        return new KitchenServiceCommandHandler(kitchenService);
    }
}
