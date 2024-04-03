FROM amazoncorretto:21-alpine-jdk
RUN apk --no-cache add curl
COPY target/dummy-ctt-0.0.1-SNAPSHOT.jar dummyctt.jar
EXPOSE 10008 8089
ENV SPRINGPROFILES=dev
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8089","-Dspring.profiles.active=${SPRINGPROFILES}","-jar","-Dserver.port=10008","dummyctt.jar"]