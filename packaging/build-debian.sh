#!/usr/bin/env bash

set -e  # Exit on error

cd docker-build
docker compose build builder-debian-11
docker compose run builder-debian-11 $@
