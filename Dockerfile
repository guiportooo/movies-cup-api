FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/movies-cup-api-0.0.1-SNAPSHOT-standalone.jar /movies-cup-api/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/movies-cup-api/app.jar"]
