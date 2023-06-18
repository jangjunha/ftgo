# ftgo

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

## Run Service via Compose

```bash
$ docker-compose up -d
```

[microservices-patterns-book]: https://microservices.io/book
