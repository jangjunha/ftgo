# ftgo

마이크로서비스 패턴 실습 프로젝트

## References

* 도서: [〈마이크로서비스 패턴〉][microservices-patterns-book]
* 원본 프로젝트: <https://github.com/microservices-patterns/ftgo-application/>


## Configure Service Database

1. postgresql 서버 설정에서 `wal_level = logical`로 설정합니다.

2. 트랜잭셔널 메시지 발행에 사용할 테이블들을 세팅합니다.

   ⚠️ 주의: 기존 테이블을 삭제합니다.

   ```bash
   psql -d ftgo-restaurant-service -f <(curl -sL 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/1.initialize-database.sql')
   psql -d ftgo-restaurant-service -f <(curl -sL 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/2.initialize-database.sql')
   psql -d ftgo-restaurant-service -f <(curl -sL 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/3.initialize-database.sql')
   psql -d ftgo-restaurant-service -f <(curl -sL 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/4.initialize-database-json.sql')
   psql -d ftgo-restaurant-service -f <(curl -sL 'https://raw.githubusercontent.com/eventuate-foundation/eventuate-common/0.17.0.RELEASE/postgres/5.initialize-database-db-id.sql')
   ```


## Run Service

```bash
$ DATABASE_URI='postgresql:///ftgo-restaurant-service' \
    ./gradlew :ftgo-order-service:bootRun
```


[microservices-patterns-book]: https://microservices.io/book
