# Use BellSoft Liberica OpenJDK 21 as the base image, which supports linux/arm64
FROM bellsoft/liberica-openjdk-debian:21

# Set working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR file from the Maven target directory
COPY target/node-service-0.0.1-SNAPSHOT.jar /app/app.jar



# Install bash to ensure shell script compatibility
RUN apt-get update && apt-get install -y bash && rm -rf /var/lib/apt/lists/*

# Expose the default Spring Boot port (adjust if your app uses a different port)
EXPOSE 8080

# Define an entrypoint that allows running the Spring Boot app or shell scripts
ENTRYPOINT ["/bin/bash", "-c"]

# Default command: run the Spring Boot app and optionally execute a shell script
CMD ["java -jar /app/app.jar"]
# Alternative CMD to run a shell script first, then the app:
# CMD ["/app/script.sh && java -jar /app/app.jar"]