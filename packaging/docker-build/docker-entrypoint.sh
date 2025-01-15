#!/usr/bin/env bash
set -ex

# execute any provided command
$@

cd ${WORKDIR}/cql-metrics-collector
dpkg-buildpackage -us -uc -b
mv ../*.deb ${WORKDIR}/packages
mv target/*.tar.gz ${WORKDIR}/packages
mv target/*.zip ${WORKDIR}/packages
dh clean
cd ${WORKDIR}
