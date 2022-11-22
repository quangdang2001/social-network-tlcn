FROM openjdk:17-jdk-alpine
MAINTAINER baeldung.com
COPY target/demo-0.0.1-SNAPSHOT.jar socialnetwork-1.0.0.jar
ENTRYPOINT ["java","-jar","/socialnetwork-1.0.0.jar"]
