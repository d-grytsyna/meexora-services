FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/payment-service-*.jar payment-service.jar
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
