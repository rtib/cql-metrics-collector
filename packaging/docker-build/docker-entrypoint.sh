#!/usr/bin/env bash
set -ex

cd ${WORKDIR}/cql-metrics-collector
dpkg-buildpackage -us -uc -b
mv ../*.deb ${WORKDIR}/packages
dh clean
cd ${WORKDIR}

# execute any provided command
$@
