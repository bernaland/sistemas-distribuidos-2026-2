FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace
COPY pom.xml mvnw .mvn/ ./
COPY src ./src
RUN ./mvnw -B -DskipTests package || mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
