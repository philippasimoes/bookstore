FROM openjdk:21-slim
RUN apt-get update && apt-get install -y curl
COPY target/shipping-service-0.0.1-SNAPSHOT.jar shippingservice.jar
EXPOSE 10006 8087
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8087","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10006","shippingservice.jar"]