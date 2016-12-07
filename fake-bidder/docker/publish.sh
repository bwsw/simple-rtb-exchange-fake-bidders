#!/usr/bin/env bash

# Build and publish docker image to bw docker registry.

if [ "$#" -ne 1 ];
then
  echo "Usage: $0 version" >&2
  exit 1
fi

docker build -t ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1 . --build-arg version=$1
docker push ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
