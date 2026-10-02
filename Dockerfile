# Builder
FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :app:buildFatJar --no-daemon

# Runner
FROM eclipse-temurin:21-jre
EXPOSE 8080

RUN addgroup --system ktor && \
    adduser --system --ingroup ktor ktoruser

RUN mkdir /app && chown ktoruser:ktor /app
COPY --chown=ktoruser:ktor --from=build /home/gradle/src/app/build/libs/*-all.jar /app/ktor-backend.jar
USER ktoruser
ENTRYPOINT ["java", "-Djava.io.tmpdir=/tmp", "-jar", "/app/ktor-backend.jar"]