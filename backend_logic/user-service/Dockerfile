# --- build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY shared/pom.xml shared/pom.xml
COPY shared/src shared/src
RUN mvn -q -DskipTests -f shared/pom.xml clean install

# 2) ora user-service può risolvere la dipendenza 'shared'
COPY user-service/pom.xml user-service/pom.xml
RUN mvn -q -DskipTests -f user-service/pom.xml dependency:go-offline
COPY user-service/src user-service/src
RUN mvn -q -DskipTests -f user-service/pom.xml package

# --- run ---
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
