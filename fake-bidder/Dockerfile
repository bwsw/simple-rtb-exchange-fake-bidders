FROM ubuntu:latest

# Install Java.
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get clean


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
