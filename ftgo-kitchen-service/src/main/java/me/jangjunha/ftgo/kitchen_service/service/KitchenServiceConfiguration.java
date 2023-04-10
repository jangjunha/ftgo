package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        EventuateTramKafkaMessageConsumerConfiguration.class,
        TramEventSubscriberConfiguration.class,
//        TramConsumerJdbcAutoConfiguration.class,  // 테이블 기반 중복 메시지 검출기 사용 시
        TramNoopDuplicateMessageDetectorConfiguration.class,
        // configure default ChannelMapping bean
        TramMessagingCommonAutoConfiguration.class,
        // configure default DomainEventNameMapping bean
        TramEventsCommonAutoConfiguration.class
})
public class KitchenServiceConfiguration {
    @Bean
    public DomainEventDispatcher domainEventDispatcher(
            KitchenServiceEventConsumer kitchenServiceEventConsumer,
            DomainEventDispatcherFactory domainEventDispatcherFactory
    ) {
        return domainEventDispatcherFactory.make("kitchenServiceEvents", kitchenServiceEventConsumer.domainEventHandlers());
    }
}
