FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/user-service-*.jar user-service.jar
ENTRYPOINT ["java", "-jar", "user-service.jar"]
