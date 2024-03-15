FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/UserService-0.0.1-SNAPSHOT.jar userservice.jar
EXPOSE 10007 8088
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8088","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10007","userservice.jar"]