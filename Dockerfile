FROM bellsoft/liberica-openjre-alpine:17 AS app-base
WORKDIR /app
VOLUME /tmp

FROM bellsoft/liberica-openjdk-alpine:17 AS build-base
WORKDIR /app
COPY . /app


### Restaurant Service ###
FROM build-base AS build-restaurant-service
ARG TARGET_PROJECT=ftgo-restaurant-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS restaurant-service
ARG DEPENDENCY=/app/ftgo-restaurant-service/build/dependency
COPY --from=build-restaurant-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-restaurant-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-restaurant-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.restaurant_service.RestaurantServiceMain"]


### Consumer Service ###
FROM build-base AS build-consumer-service
ARG TARGET_PROJECT=ftgo-consumer-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS consumer-service
ARG DEPENDENCY=/app/ftgo-consumer-service/build/dependency
COPY --from=build-consumer-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-consumer-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-consumer-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.consumer_service.ConsumerServiceApplicationKt"]


### Kitchen Service ###
FROM build-base AS build-kitchen-service
ARG TARGET_PROJECT=ftgo-kitchen-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS kitchen-service
ARG DEPENDENCY=/app/ftgo-kitchen-service/build/dependency
COPY --from=build-kitchen-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-kitchen-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-kitchen-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.kitchen_service.KitchenServiceApplication"]


### Order Service ###
FROM build-base AS build-order-service
ARG TARGET_PROJECT=ftgo-order-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS order-service
ARG DEPENDENCY=/app/ftgo-order-service/build/dependency
COPY --from=build-order-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-order-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-order-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.order_service.OrderServiceApplicationKt"]


### Accounting Service ###
FROM build-base AS build-accounting-service
ARG TARGET_PROJECT=ftgo-accounting-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS accounting-service
ARG DEPENDENCY=/app/ftgo-accounting-service/build/dependency
COPY --from=build-accounting-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-accounting-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-accounting-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.accounting_service.AccountingServiceApplicationKt"]


### Order History Service ###
FROM build-base AS build-order-history-service
ARG TARGET_PROJECT=ftgo-order-history-service
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS order-history-service
ARG DEPENDENCY=/app/ftgo-order-history-service/build/dependency
COPY --from=build-order-history-service ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-order-history-service ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-order-history-service ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.order_history_service.OrderHistoryServiceMainKt"]


### API Gateway ###
FROM build-base AS build-api-gateway
ARG TARGET_PROJECT=ftgo-api-gateway
ENV TARGET_PROJECT=${TARGET_PROJECT}
RUN --mount=type=cache,target=/root/.gradle ./gradlew \
      clean build \
      -p ${TARGET_PROJECT}
RUN mkdir -p ${TARGET_PROJECT}/build/dependency && (cd ${TARGET_PROJECT}/build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM app-base AS api-gateway
ARG DEPENDENCY=/app/ftgo-api-gateway/build/dependency
COPY --from=build-api-gateway ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build-api-gateway ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build-api-gateway ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp",".:./lib/*","me.jangjunha.ftgo.apigateway.APIGatewayApplicationKt"]
