# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
# define repo
WORKDIR /app
# Copy project
COPY . .
# Build jar
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
# retrieve jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]