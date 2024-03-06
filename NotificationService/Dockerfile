FROM openjdk:21-slim
COPY target/NotificationService-0.0.1-SNAPSHOT.jar notification-service.jar
EXPOSE 10002 8083
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8083","-jar","notification-service.jar"]