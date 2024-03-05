FROM openjdk:21-slim
COPY target/StockService-0.0.1-SNAPSHOT.jar stock-service.jar
EXPOSE 10001 8082
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8083","-jar","stock-service.jar"]