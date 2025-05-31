FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/gateway-service-*.jar gateway-service.jar
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
