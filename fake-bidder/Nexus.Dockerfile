FROM openjdk:8-alpine

# Define working directory.
WORKDIR /data

ARG URL
ARG USERNAME
ARG PASSWORD

# Download rtb-exchange from nexus.
RUN \
    export REPO_TYPE="$(echo "${VERSION}" | sed -n 's/.*SNAPSHOT.*/-snapshot/p')" && \
    wget --user=${USERNAME} --password=${PASSWORD} -O bidder-mock.jar ${URL}

ENV env=

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "bidder-mock.jar", "-p", "8083", "-h", "0.0.0.0"]

EXPOSE 8083
