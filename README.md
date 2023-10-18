# ftgo

[![Pact](https://img.shields.io/badge/pact_broker-dashboard-454CF0)](https://pact.ftgo.jangjunha.me/)

마이크로서비스 패턴 실습 프로젝트

## References

* 도서: [〈마이크로서비스 패턴〉][microservices-patterns-book]
* 원본 프로젝트: <https://github.com/microservices-patterns/ftgo-application/>

## 서비스와 관련 챕터

* [Restaurant Service](./ftgo-restaurant-service) 매장, 메뉴 서비스
* [Consumer Service](./ftgo-consumer-service) (배달 받는) 소비자 서비스 — 3장) 기본 메시징
* [Kitchen Service](./ftgo-kitchen-service) 주방 서비스
* [**Order Service**](./ftgo-order-service) 주문 서비스 — 4,5장) 애그리거트 패턴 사용 및 오케스트레이션 사가 구현
* [**Accounting Service**](./ftgo-accounting-service) 회계 서비스 — 6장) 이벤트 소싱 기법 사용
* [**Order History Service**](./ftgo-order-history-service) 주문 내역 서비스 — 7장) CQRS, DynamoDB 사용
* [API Gateway](./ftgo-api-gateway) Spring Cloud Gateway를 사용한 API Gateway 구현 — 8장) 외부 API 패턴

## 테스트 종류별 대표 예제

* 단위 테스트
  * Entity — [OrderTest](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/domain/OrderTest.kt)
  * Value Object — [MoneyTest](./ftgo-common/src/test/java/me/jangjunha/ftgo/common/MoneyTest.java)
  * Saga — [CreateOrderSagaTest](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/sagas/createorder/CreateOrderSagaTest.kt)
  * Domain Service — [OrderServiceTest](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/service/OrderServiceTest.kt)  
  * Controller — [OrderControllerTest](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/web/OrderControllerTest.kt)
  * Event / Message Handler — [OrderEventConsumerTest](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/messaging/OrderEventConsumerTest.kt)
* 통합 테스트
  * 영속화 테스트 — [OrderJpaTest](./ftgo-order-service/src/integration-test/kotlin/me/jangjunha/ftgo/order_service/domain/OrderJpaTest.kt)
  * **소비자 주도 계약 테스트** : using [Pact][pact]
    * REST 요청/응답형 상호 작용 — [API Gateway](./ftgo-api-gateway/src/test/kotlin/me/jangjunha/ftgo/apigateway/proxies/RestaurantServicePactTest.kt) ➡️ [매장 서비스](./ftgo-restaurant-service/src/test/java/me/jangjunha/ftgo/restaurant_service/HttpPactProviderTest.java) (매장 조회)
    * gRPC 요청/응답형 상호 작용 — [API Gateway](./ftgo-api-gateway/src/test/kotlin/me/jangjunha/ftgo/apigateway/proxies/KitchenServicePactTest.kt) ➡️ [주방 서비스](./ftgo-kitchen-service/src/test/java/me/jangjunha/ftgo/kitchen_service/GrpcPactProviderTest.java) (티켓 조회)
    * 발행/구독 스타일 상호 작용 — [주방 서비스](./ftgo-kitchen-service/src/test/java/me/jangjunha/ftgo/kitchen_service/RestaurantServicePactTest.java) ➡️ [매장 서비스](./ftgo-restaurant-service/src/test/java/me/jangjunha/ftgo/restaurant_service/MessagingPactProviderTest.java) (메뉴 변경 이벤트) 
    * 비동기 요청/응답형 상호 작용 — [주문 서비스](./ftgo-order-service/src/test/kotlin/me/jangjunha/ftgo/order_service/sagaparticipants/ConsumerTestProxyPactTest.kt) ➡️ [소비자 서비스](./ftgo-consumer-service/src/test/kotlin/me/jangjunha/ftgo/consumer_service/CommandPactProviderTest.kt) (주문 생성 사가 / 소비자 검증) 


## 연관 저장소

* [jangjunha/ftgo-proto][ftgo-proto] Protobuf/gRPC 인터페이스 라이브러리
* [jangjunha/ftgo-graphql-server][ftgo-graphql-server] GraphQL version of API Gateway - 8장) 외부 API 패턴
* [jangjunha/ftgo-web][ftgo-web] MVP 웹사이트

## Run Service via Compose

```bash
$ docker-compose up -d
```

[microservices-patterns-book]: https://microservices.io/book
[ftgo-proto]: https://github.com/jangjunha/ftgo-proto/
[ftgo-graphql-server]: https://github.com/jangjunha/ftgo-graphql-server/
[ftgo-web]: https://github.com/jangjunha/ftgo-web/
[pact]: https://docs.pact.io
