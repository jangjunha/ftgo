# ftgo

마이크로서비스 패턴 실습 프로젝트

## References

* 도서: [〈마이크로서비스 패턴〉][microservices-patterns-book]
* 원본 프로젝트: <https://github.com/microservices-patterns/ftgo-application/>


## Run Service

```bash
$ DATABASE_URI='postgresql:///ftgo-restaurant-service' \
    ./gradlew :ftgo-order-service:bootRun
```


[microservices-patterns-book]: https://microservices.io/book
