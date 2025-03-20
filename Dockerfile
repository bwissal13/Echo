# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml /app
COPY src /app/src

# Package the application using Maven
RUN apt-get update && \
    apt-get install -y maven && \
    mvn -f /app/pom.xml clean package

# Copy the packaged jar file into the container
COPY target/echo01-1.0-SNAPSHOT.jar /app/echo01.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/echo01.jar"] 