# --- build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# 1️⃣ Build del modulo 'shared'
COPY shared/pom.xml shared/pom.xml
COPY shared/src shared/src
RUN mvn -f shared/pom.xml clean install -DskipTests

# 🔹 (Fix) copia il jar di shared nel repository locale Maven
RUN mkdir -p /root/.m2/repository/com/dietiestates/shared && \
    cp -r /workspace/shared/target/* /root/.m2/repository/com/dietiestates/shared/

# 2️⃣ Build del modulo 'user-service'
COPY user-service/pom.xml user-service/pom.xml
RUN mvn -q -DskipTests -f user-service/pom.xml dependency:go-offline
COPY user-service/src user-service/src
RUN mvn -q -DskipTests -f user-service/pom.xml package

# --- run ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# *************************************************************
# Aggiunto: Copia la chiave pubblica dalla Root nella cartella /app
# *************************************************************
COPY config/public.pem.b64 /app/public.pem.b64

COPY --from=build /workspace/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]