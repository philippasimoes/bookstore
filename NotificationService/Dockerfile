FROM openjdk:21-slim
COPY target/NotificationService-0.0.1-SNAPSHOT.jar notification-service.jar
EXPOSE 10001 8084
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8084","-jar","notification-service.jar"]