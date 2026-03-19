# Build stage
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
RUN useradd -m -s /bin/false appuser
# VOLUME /tmp
COPY --chown=appuser:appuser --from=build /app/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java","-jar","/app.jar"]
