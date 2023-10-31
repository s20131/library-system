FROM eclipse-temurin:21.0.1_12-jre-alpine
ARG JAR_FILE=build/libs/library-system-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} library-system.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/library-system.jar"]
EXPOSE 8080
