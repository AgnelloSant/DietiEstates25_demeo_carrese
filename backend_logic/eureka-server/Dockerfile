# --- build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copia il POM e scarica le dipendenze (cache più efficiente)
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# copia il codice e compila
COPY src ./src
RUN mvn -q -DskipTests package

# --- run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# *************************************************************


COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
