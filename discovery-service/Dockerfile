FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/discovery-service-*.jar discovery-service.jar
ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
