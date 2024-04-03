FROM amazoncorretto:21-alpine-jdk
RUN apk --no-cache add curl

# install

# label for the image
LABEL Description="Eureka Server" Version="0.0.1"

# the version of the archive
ARG VERSION=0.0.1

# mount the temp volume
VOLUME /tmp

# Add the service as app.jar
ADD target/discovery-server-${VERSION}-SNAPSHOT.jar app.jar

EXPOSE 8761

# entrypoint to the image on run
ENTRYPOINT ["java","-jar","/app.jar"]