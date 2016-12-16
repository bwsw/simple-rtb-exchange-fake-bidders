Fake bidder
===========

This application provides fake RTB bidders used for e2e testing.

Introduction
------------

Fake bidder allows to emulate bid and win notice requests with a specified timeout.

Requirements
------------

* [sbt](http://www.scala-sbt.org/)
* [Docker](https://www.docker.com/) 

Usage
------

### Build jar file with dependencies

The command `sbt assembly` builds *target/scala-2.11/fake-bidder-\<VERSION\>-jar-with-dependencies.jar*.

### Build docker image

This project provides two options for docker images:

using Nexus
    
    docker build -t <image name> -f Nexus.dockerfile --build-arg URL=<nexus URL> --build-arg USERNAME=<nexus user> --build-arg PASSWORD=<nexus user password> .
    
using a ready jar
    
    docker build -t <image name> --build-arg APP_PATH=<path to jar with dependencies> .
    
### Run application via jar file
    
    java -jar <PATH-TO-JAR> -p <port> -h <host>
    
* *\<port\>* &mdash; bind port.
* *\<host\>* &mdash; bind address.

### Run application in docker
    
    docker run -d -p<port>:8083 <image_name>
    
* *\<port\>* &mdash; bind port. Default value is *8083*.
* *\<image_name\>* &mdash; name of docker image.
 
### Send bid request

**http://\<host\>:\<port\>?timeout=\<timeout\>\&price=\<price\>\&modifier=\<modifier\>\&winhost=\<winhost\>**

#### Parameters:

- **\<host\>** - application host,
- **\<port\>** - application port,
- **\<timeout\>** - time to wait before sending response,
- **\<price\>** - bid price,
- **\<modifier\>** - bid response modifier, possible options:
    * invalidjson - JSON with invalid syntax,
    * invaliddata - invalid data,
    * nobidnocontent - empty body and 204 status code,
    * nobidemptyjson - empty JSON object,
    * nobidemptyseatbid - empty seatbid array,
    * winnotice-withoutadm - both nurl and adm in response,
    * winnotice-withadm - only nurl without adm in response,
    * winnotice-broken - only nurl in response, returning 500 status code,
    * winnotice-timeout-**\<win-timeout\>** - only nurl in response, returning adm after **\<win-timeout\>**,
- **\<winhost\>** - host for win notice, required if winnotice-* modifier specified.

### Send win notice request

**http://\<host\>:\<port\>/win-notice?modifier=\<modifier\>\&type=\<type\>**

#### Parameters:

- **\<host\>** - application host,
- **\<port\>** - application port,
- **\<modifier\>** - win notice modifier, possible options:
    * winnotice-withoutadm - 200 status code and empty body,
    * winnotice-withadm - 200 status code and ad markup in body,
    * winnotice-timeout-**\<win-timeout\>** - 200 status code and ad markup in body after **\<win-timeout\>**,
- **\<modifier\>** - ad markup type, possible options:
    * 1 - banner,
    * 2 - native,
    * 3 - video.

