FROM eclipse-temurin:21-jre-alpine

LABEL authors="Nikolas"

WORKDIR /app

copy target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]