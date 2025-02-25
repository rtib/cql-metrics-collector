#!/usr/bin/env bash

set -xe

cd ${WORKDIR}/cql-metrics-collector

export DEBFULLNAME="Tibor Repasi"
export DEBEMAIL="rtib@users.noreply.github.com"
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -e '^[[:digit:]]')
changes_description="Release ${VERSION} of CQL-Metrics-Collector for Apache Cassandra."
dch --package "cql-metrics-collector" --newversion "${VERSION}" "${changes_description}"
dch -r --distribution stable ignored
