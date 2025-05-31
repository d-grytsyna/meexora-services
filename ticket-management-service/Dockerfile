FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/ticket-management-service-*.jar ticket-management-service.jar
ENTRYPOINT ["java", "-jar", "ticket-management-service.jar"]