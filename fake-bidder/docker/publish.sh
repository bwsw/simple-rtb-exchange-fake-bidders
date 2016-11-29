#!/usr/bin/env bash

# Build and publish docker image to bw docker registry.

docker build -t private/bidder-mocks . --build-arg version=$1
docker login -u rtb-dev-team -p rtb-dev-team-password ci.bw-sw.com:5000
docker tag private/bidder-mocks ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
docker push ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
