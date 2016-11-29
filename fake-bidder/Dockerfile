FROM ubuntu:latest

# Install Java.
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer


# Define working directory.
WORKDIR /data

# Define commonly used JAVA_HOME variable.
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

ARG version

# Download fake-bidder from nexus.
RUN wget --user=deployment --password=Bg3MWyM54Mtq4tK8 "http://rtb-ci.z1.netpoint-dc.com:8081/nexus/service/local/artifact/maven/content?r=bitworks-rtb&g=com.bitworks&a=fake-bidder_2.11&v=${version}&c=assembly" -O bidder-mock.jar

# Run fake-bidder.
ENTRYPOINT ["java", "-jar", "bidder-mock.jar", "-p", "8083"]

EXPOSE 8083
