package me.jangjunha.ftgo.kitchen_service;

import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		EventuateTramKafkaMessageConsumerConfiguration.class,
		TramMessageProducerJdbcConfiguration.class,
		TramNoopDuplicateMessageDetectorConfiguration.class,
//        TramConsumerJdbcAutoConfiguration.class,  // 테이블 기반 중복 메시지 검출기 사용 시
})
public class KitchenServiceApplication {

	public static void main(String[] args) {
		JSonMapper.objectMapper.findAndRegisterModules();
		SpringApplication.run(KitchenServiceApplication.class, args);
	}

}
