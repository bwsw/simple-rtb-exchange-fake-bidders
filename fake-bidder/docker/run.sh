#!/usr/bin/env bash

# Pulls and starts docker container.

if [ "$#" -ne 1 ];
then
  echo "Usage: $0 version" >&2
  exit 1
fi

docker pull ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
docker stop bidder-mocks || true && docker rm bidder-mocks || true
docker run --name bidder-mocks -d -p 8083:8083 ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
