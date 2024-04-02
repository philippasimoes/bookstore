FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/return-service-0.0.1-SNAPSHOT.jar returnservice.jar
EXPOSE 10005 8086
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8086","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10005","returnservice.jar"]