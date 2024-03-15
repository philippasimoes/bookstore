FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/OrderService-0.0.1-SNAPSHOT.jar orderservice.jar
EXPOSE 10003 8084
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8084","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10003","orderservice.jar"]