FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD SpringBoot-Docker-1.0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9083
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]