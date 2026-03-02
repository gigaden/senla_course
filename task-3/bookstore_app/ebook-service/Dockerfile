FROM maven:3.9-amazoncorretto-17-alpine AS build
WORKDIR /app
COPY pom.xml ./pom.xml
COPY ebook-service ./ebook-service

RUN mvn -f pom.xml clean package -pl ebook-service -am -DskipTests

FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=build /app/ebook-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]