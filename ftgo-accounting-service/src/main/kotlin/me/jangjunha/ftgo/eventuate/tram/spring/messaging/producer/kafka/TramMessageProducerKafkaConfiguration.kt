package me.jangjunha.ftgo.eventuate.tram.spring.messaging.producer.kafka

import io.eventuate.messaging.kafka.common.EventuateKafkaConfigurationProperties
import io.eventuate.messaging.kafka.producer.EventuateKafkaProducer
import io.eventuate.messaging.kafka.producer.EventuateKafkaProducerConfigurationProperties
import io.eventuate.messaging.kafka.spring.common.EventuateKafkaPropertiesConfiguration
import io.eventuate.tram.messaging.producer.common.MessageProducerImplementation
import io.eventuate.tram.spring.messaging.producer.common.TramMessagingCommonProducerConfiguration
import me.jangjunha.ftgo.eventuate.tram.producer.kafka.MessageProducerKafkaImpl
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramMessagingCommonProducerConfiguration::class,
    EventuateKafkaPropertiesConfiguration::class,
])
class TramMessageProducerKafkaConfiguration {
    @Bean
    @ConditionalOnMissingBean(MessageProducerImplementation::class)
    fun messageProducerImplementation(
        kafkaProducer: EventuateKafkaProducer,
    ): MessageProducerImplementation {
        return MessageProducerKafkaImpl(kafkaProducer)
    }

    @Bean
    fun eventuateKafkaProducer(
        properties: EventuateKafkaConfigurationProperties,
    ): EventuateKafkaProducer {
        return EventuateKafkaProducer(
            properties.bootstrapServers,
            EventuateKafkaProducerConfigurationProperties(),
        )
    }
}
