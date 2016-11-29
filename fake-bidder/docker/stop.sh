#!/usr/bin/env bash

# Stops and removes docker container.

docker stop bidder-mocks || true && docker rm bidder-mocks || true

