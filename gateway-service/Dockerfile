FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
EXPOSE 8080
COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
