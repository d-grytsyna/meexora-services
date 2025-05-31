FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/auth-service-*.jar auth-service.jar
ENTRYPOINT ["java", "-jar", "auth-service.jar"]