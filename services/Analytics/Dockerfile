FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
EXPOSE 50051:50051
EXPOSE 50052:50052
RUN bash mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/analytics.jar
ENTRYPOINT ["java","-jar","/app/analytics.jar"]