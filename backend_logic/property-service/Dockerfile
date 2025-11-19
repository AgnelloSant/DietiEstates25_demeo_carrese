# --- build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1) build 'shared'
COPY shared/pom.xml shared/pom.xml
COPY shared/src shared/src
RUN mvn -q -DskipTests -f shared/pom.xml clean install

# 2) build 'property-service'
COPY property-service/pom.xml property-service/pom.xml
RUN mvn -q -DskipTests -f property-service/pom.xml dependency:go-offline
COPY property-service/src property-service/src
RUN mvn -q -DskipTests -f property-service/pom.xml package

# --- run ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# *************************************************************
# Aggiunto: Copia la chiave pubblica dalla Root nella cartella /app
# *************************************************************
COPY config/public.pem.b64 /app/public.pem.b64

COPY --from=build /workspace/property-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]