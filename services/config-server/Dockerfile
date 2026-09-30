# Build from the repository root: docker build -f services/config-server/Dockerfile .
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY services/ services/
RUN mvn -B -f services/pom.xml -pl config-server -am package -DskipTests
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/services/config-server/target/*.jar app.jar
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
