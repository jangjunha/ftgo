package me.jangjunha.ftgo.restaurant_service.service;

import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({
        // configure MessageProducer bean
        TramMessageProducerJdbcConfiguration.class,
        // configure DomainEventPublisher bean
        TramEventsPublisherConfiguration.class,
        // configure default ChannelMapping bean
        TramMessagingCommonAutoConfiguration.class,
        // configure default DomainEventNameMapping bean
        TramEventsCommonAutoConfiguration.class
})
public class RestaurantServiceConfiguration {
}
