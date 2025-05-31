FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/email-service-*.jar email-service.jar
ENTRYPOINT ["java", "-jar", "email-service.jar"]
