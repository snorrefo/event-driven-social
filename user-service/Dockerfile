FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :user-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/user-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
