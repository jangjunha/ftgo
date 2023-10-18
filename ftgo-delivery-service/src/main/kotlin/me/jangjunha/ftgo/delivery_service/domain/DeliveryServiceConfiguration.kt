package me.jangjunha.ftgo.delivery_service.domain

import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import io.grpc.ManagedChannelBuilder
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceGrpcKt
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableConfigurationProperties(Destinations::class)
@Import(
    TramMessageProducerJdbcConfiguration::class,
    TramEventsPublisherConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
)
class DeliveryServiceConfiguration {
    @Bean
    fun kitchenService(destinations: Destinations): KitchenServiceGrpcKt.KitchenServiceCoroutineStub =
        KitchenServiceGrpcKt.KitchenServiceCoroutineStub(
            ManagedChannelBuilder.forTarget(destinations.kitchenServiceUrl).usePlaintext().build()
        )
}
