FROM amazoncorretto:21-alpine-jdk
RUN apk --no-cache add curl
COPY target/catalog-service-0.0.1-SNAPSHOT.jar catalogservice.jar
EXPOSE 10000 8081
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10000","catalogservice.jar"]