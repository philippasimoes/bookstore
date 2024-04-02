FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/stock-service-0.0.1-SNAPSHOT.jar stock-service.jar
EXPOSE 10001 8082
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8082","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10001","stock-service.jar"]