# Build from the repository root: docker build -f services/customer-service/Dockerfile .
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
COPY services/ services/
RUN mvn -B -f services/pom.xml -pl customer-service -am package -DskipTests
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/customer-service/target/*.jar app.jar
USER 10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
