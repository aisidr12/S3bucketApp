FROM eclipse-temurin:17-jdk-alpine
LABEL author=ArturoIsidroh
COPY target/S3BucketApp-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]