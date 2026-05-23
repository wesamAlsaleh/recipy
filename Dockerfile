# ============================================================
# STAGE 1: BUILD
# Use a Maven image that already has JDK 17 installed
# This stage compiles the source code and packages it into a .jar file
# This image is ONLY used for building — it won't be in the final image
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build

# Set the working directory inside the container to /app
# All subsequent commands will run from this directory
WORKDIR /app

# Copy the Maven project descriptor first (before source code)
# This is a Docker optimization — if pom.xml hasn't changed,
# Docker will use the cached layer and skip re-downloading dependencies
COPY pom.xml .

# Copy the entire source code into the container
COPY src ./src

# Run Maven to compile and package the app into a .jar file
# -DskipTests skips running tests to speed up the build
# The output will be at: /app/target/recipy-0.0.1-SNAPSHOT.jar
RUN mvn package -DskipTests


# ============================================================
# STAGE 2: RUN
# Use a lightweight JRE image (JRE = Java Runtime Environment)
# JRE is smaller than JDK because it can only RUN Java, not compile it
# We don't need the full Maven + JDK image in production — just the .jar
# ============================================================
FROM eclipse-temurin:17-jre

# Create a temporary directory that Docker can use for I/O operations
# Spring Boot uses /tmp internally for embedded Tomcat
VOLUME /tmp

# Tell Docker that this container will listen on port 8080
# Note: this does NOT actually publish the port — that's done in docker-compose.yml
EXPOSE 8080

# Create the directory where our app will live
RUN mkdir -p /app/

# Create the directory where our app will write logs
RUN mkdir -p /app/logs/

# Copy the .jar file from Stage 1 (the build stage) into Stage 2 (the run stage)
# --from=build refers to the first stage we named "build" above
# This is the key of multi-stage builds — we only carry over what we need
COPY --from=build /app/target/recipy-0.0.1-SNAPSHOT.jar /app/app.jar

# Define the command that runs when the container starts
# -Djava.security.egd speeds up startup by using a faster random number source
# -Dspring.profiles.active=docker tells Spring Boot to use application-docker.yml
# -jar /app/app.jar points to our packaged application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker", "-jar", "/app/app.jar"]