FROM amazoncorretto:21-alpine-jdk
RUN apk --no-cache add curl
COPY target/payment-service-0.0.1-SNAPSHOT.jar paymentservice.jar
EXPOSE 10004 8085
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8085","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10004","paymentservice.jar"]