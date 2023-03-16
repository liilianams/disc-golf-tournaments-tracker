# Build Stage
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build --exclude-task test

# Package Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]