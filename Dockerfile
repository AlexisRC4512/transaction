FROM amazoncorretto:17-alpine
WORKDIR /app
# Copy the application JAR file into the Docker image
COPY ./target/transaction-0.0.1-SNAPSHOT.jar /app

# Expose port 8167 to access the application from outside the container
EXPOSE 8167

# Run the application
ENTRYPOINT ["java", "-jar", "transaction-0.0.1-SNAPSHOT.jar"]