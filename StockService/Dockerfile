FROM openjdk:21-slim
COPY target/StockService-0.0.1-SNAPSHOT.jar stock-service.jar
EXPOSE 10000 8083
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8083","-jar","stock-service.jar"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]