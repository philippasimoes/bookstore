FROM openjdk:21-slim
COPY target/CatalogService-0.0.1-SNAPSHOT.jar catalogservice.jar
EXPOSE 10000 8081
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081","-jar","catalogservice.jar"]