# --- build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copia il POM e scarica le dipendenze (cache più efficiente)
COPY eureka-server/pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# copia il codice e compila
COPY eureka-server/src ./src
RUN mvn -q -DskipTests package

# --- run stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# *************************************************************
# Aggiunto: Copia la chiave pubblica dalla Root nella cartella /app
# Assumendo che la chiave sia accessibile dalla Root del progetto
# *************************************************************
COPY config/public.pem.b64 /app/public.pem.b64

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
