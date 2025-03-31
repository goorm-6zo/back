FROM eclipse-temurin:23-jdk-jammy as build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test
FROM eclipse-temurin:23-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY src/main/resources/application.yml ./src/main/resources/application.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
