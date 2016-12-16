FROM openjdk:8-alpine

# Define working directory.
WORKDIR /opt/fake-bidder

ARG APP_PATH

COPY $APP_PATH fake-bidder.jar

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "fake-bidder.jar", "-p", "8083", "-h", "0.0.0.0"]

EXPOSE 8083
