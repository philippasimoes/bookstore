FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/notification-service-0.0.1-SNAPSHOT.jar notification-service.jar
EXPOSE 10002 8083
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8083","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10002","notification-service.jar"]