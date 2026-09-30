# Build from the repository root: docker build -f services/catalog-service/Dockerfile .
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY services/ services/
RUN mvn -B -f services/pom.xml -pl catalog-service -am package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/services/catalog-service/target/*.jar app.jar
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
