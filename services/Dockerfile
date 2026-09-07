FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY services/ services/
RUN mvn -B -f services/pom.xml package -DskipTests
FROM eclipse-temurin:17-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /workspace/services/${SERVICE}/target/*.jar app.jar
USER 10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
