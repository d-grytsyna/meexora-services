FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/event-service-*.jar event-service.jar
ENTRYPOINT ["java", "-jar", "event-service.jar"]
