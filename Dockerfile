#*********************************************************
#OLD WAY without creating jar using Docker

#FROM eclipse-temurin:17-jdk
#WORKDIR /user-service
#COPY target/user-service-1.0-SNAPSHOT.jar user-service.jar
#ENTRYPOINT ["java", "-jar", "user-service.jar"]
#*********************************************************


# ================================
# 1. BUILD STAGE - MAVEN COMPILATION
# ================================

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first
COPY pom.xml .

# Download Maven dependencies
RUN mvn -e -B dependency:go-offline

# Copy application source code
COPY src ./src

# Compile + package application
RUN mvn -e -B clean package -DskipTests


# ================================
# 2. RUN STAGE - APPLICATION
# ================================

FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy generated jar from build stage
COPY --from=builder /app/target/*.jar user-service.jar

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "user-service.jar"]

#During build (using builder stage):
#/app
#   ├── pom.xml
#   ├── src/
#   ├── target/
#   │     └── user-service.jar