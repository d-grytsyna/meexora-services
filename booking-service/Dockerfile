FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/booking-service-*.jar booking-service.jar
ENTRYPOINT ["java", "-jar", "booking-service.jar"]
