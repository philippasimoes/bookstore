#
# Build stage
#
FROM maven:3.9.6-amazoncorretto-21 AS MAVEN_TOOL_CHAIN

COPY pom.xml /tmp
COPY CatalogService /tmp/CatalogService
COPY StockService /tmp/StockService
WORKDIR /tmp/
RUN mvn clean install -Pdocker

FROM openjdk:21-jdk-oracle
COPY --from=MAVEN_TOOL_CHAIN /tmp/CatalogService/target/CatalogService-0.0.1-SNAPSHOT.jar catalogservice.jar

RUN sh -c 'touch /catalogservice.jar'
EXPOSE 8080 8081 8082 8083
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081","-jar","catalogservice.jar"]