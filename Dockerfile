FROM bellsoft/liberica-openjre-alpine:17 AS app-base
WORKDIR /app
VOLUME /tmp

FROM bellsoft/liberica-openjdk-alpine:17 AS build-base
WORKDIR /app
COPY . /app

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
