FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :post-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/post-service/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
