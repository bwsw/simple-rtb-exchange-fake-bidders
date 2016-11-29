#!/usr/bin/env bash

# Pulls and starts docker container.

docker login -u rtb-dev-team -p rtb-dev-team-password ci.bw-sw.com:5000
docker pull ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
docker run --rm -p 8083:8083 ci.bw-sw.com:5000/rtb-dev-team/bidder-mocks:$1
