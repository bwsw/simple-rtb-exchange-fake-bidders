FROM openjdk:8-alpine

# Define working directory.
WORKDIR /opt/fake-bidder

ARG URL
ARG USERNAME
ARG PASSWORD

# Download rtb-exchange from nexus.
RUN wget --user=${USERNAME} --password=${PASSWORD} -O fake-bidder.jar ${URL}

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "fake-bidder.jar", "-p", "8083", "-h", "0.0.0.0"]

EXPOSE 8083
