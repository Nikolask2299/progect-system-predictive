FROM eclipse-temurin:21-jre-alpine
LABEL authors="Nikolas"

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 9093

ENTRYPOINT ["java", "-jar", "app.jar"]