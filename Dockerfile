# Stage 1: Build WAR using Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
RUN mvn clean package

# Stage 2: Deploy WAR to Tomcat
FROM tomcat:9.0-jdk17
COPY --from=builder /app/target/EventManagementSystemV2.war /usr/local/tomcat/webapps/
EXPOSE 8080
