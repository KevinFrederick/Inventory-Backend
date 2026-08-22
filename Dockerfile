# Builder
FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :app:buildFatJar --no-daemon

# Runner
FROM eclipse-temurin:21-jre
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/app/build/libs/*-all.jar /app/ktor-backend.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-backend.jar"]