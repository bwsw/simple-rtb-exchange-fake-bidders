FROM openjdk:8-alpine

# Define working directory.
WORKDIR /data

ARG APP_PATH

COPY $APP_PATH bidder-mock.jar

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "bidder-mock.jar", "-p", "8083", "-h", "0.0.0.0"]

EXPOSE 8083
