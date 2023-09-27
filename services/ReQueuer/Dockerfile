FROM bellsoft/liberica-openjdk-alpine:20

# settled in build.gradle.kts
COPY *.jar requeuer.jar

EXPOSE 50052:50052

CMD ["java", "-jar", "requeuer.jar"]