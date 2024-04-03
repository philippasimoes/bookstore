FROM amazoncorretto:21-alpine-jdk
RUN apk --no-cache add curl
COPY target/shipping-service-0.0.1-SNAPSHOT.jar shippingservice.jar
EXPOSE 10006 8087
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8087","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10006","shippingservice.jar"]