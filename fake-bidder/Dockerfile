FROM ci.bw-sw.com:5000/rtb-dev-team/oracle-java:8

# Define working directory.
WORKDIR /data

ARG version

# Download fake-bidder from nexus.
RUN \
    wget --user=deployment --password=Bg3MWyM54Mtq4tK8 -O bidder-mock.jar \
        "http://rtb-ci.z1.netpoint-dc.com:8081/nexus/service/local/artifact/maven/content?r=bitworks-rtb&g=com.bitworks&a=fake-bidder_2.11&v=${version}&c=jar-with-dependencies"

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "bidder-mock.jar", "-p", "8083", "-h", "0.0.0.0"]

EXPOSE 8083
