# ftgo-accounting-service

이벤트 소싱 기법을 활용한 서비스. 책의 예제와 달리 EventStoreDB를 사용합니다.

Aggregate의 메소드는 이벤트를 생산하고 apply 메소드를 사용하여 이벤트를 애그리거트에 적용합니다.

## 노트

### Projection 사이클

`EventStoreDBSubscriptionWorker`가 이벤트 저장소를 구독하여 RDB로 투사(project)합니다. 여기에서 `ApplicationEventPublisher`로 이벤트를 발행하면, 투영(
projection)들에서 `@EventListener` 애노테이션을 붙인 핸들러들이 관심있는 이벤트를 구독하여 RDB에 저장합니다.

### 애그리거트와 서비스

애그리거트의 메소드를 호출하면 이벤트(들)을 반환합니다. 메소드는 애그리거트를 직접 수정하지 않습니다. 반환된 이벤트로 애그리거트의 `apply()` 메소드를 호출하여 이벤트가 적용된 결과를 얻을 수 있습니다.

서비스는 애그리거트 스토어를 사용하여 애그리거트에 접근하고 수정합니다.

### 원자적 사가 커맨드 응답

서비스의 메소드들은 사가 응답 메시지를 원자적을 발행하기 위해 선택적 `replyingHeaders` 인자를 가집니다. 인자가 주어지면 이벤트 스트림에 메소드 실행 성공 여부를
담은 `SagaReplyRequested` 이벤트를 함께 씁니다. (실패하면 실패 응답을 요청하는 `SagaReplyRequested` 이벤트를 씁니다)

`SagaReplyRequestedEventSubscriptionWorker`는 `SagaReplyRequested` 이벤트 타입을 구독하여 응답 메시지를 결과적으로(eventually) 발행합니다.

### 중복 메시지 처리

서비스의 메소드들은 선택적으로 중복 메시지 처리를 위해 `eventId` 인자를 받습니다. 이는 이벤트 스토어에 기록하는 이벤트의 ID로 사용됩니다. EventStoreDB는 같은 이벤트 ID로 여러 이벤트를
기록하는 경우 하나의 이벤트만 기록합니다.[[문서]](https://developers.eventstore.com/clients/grpc/appending-events.html#eventid)

### Eventuate Tram Message Producer _Kafka_

Kafka producer 구현체가 제공되지 않아서 [`:eventuate-tram-producer-kafka`](../eventuate-tram-producer-kafka)
모듈에 `MessageProducerKafkaImpl` 구현체를 직접 구현하여 사용합니다. [JDBC producer 구현체][eventuate-tram-producer-jdbc-ref]
와 [eventuate-cdc][eventuate-cdc-kafka-ref]를 참고하여 구현했습니다.

`TramMessageProducerKafkaConfiguration`을 import하여 kafka producer를 등록할 수 있습니다.

## References

* <https://github.com/oskardudycz/EventSourcing.JVM/>

[eventuate-tram-producer-jdbc-ref]: https://github.com/eventuate-tram/eventuate-tram-core/blob/e224387364a8a31dded132a0649b164e00894256/eventuate-tram-producer-jdbc/src/main/java/io/eventuate/tram/messaging/producer/jdbc/MessageProducerJdbcImpl.java

[eventuate-cdc-kafka-ref]: https://github.com/eventuate-foundation/eventuate-cdc/blob/8d56a1315af2f4e213661e1a693a1229b19db4b1/eventuate-cdc-data-producer-wrappers/src/main/java/io/eventuate/cdc/producer/wrappers/kafka/EventuateKafkaDataProducerWrapper.java
