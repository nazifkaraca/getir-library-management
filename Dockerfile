FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/library-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8070

ENTRYPOINT ["java", "-jar", "app.jar"]
