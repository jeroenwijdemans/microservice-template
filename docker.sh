#!/usr/bin/env bash


DIR="$( cd "$( dirname "\${BASH_SOURCE[0]}" )" && pwd )"

echo "Creating application ..."
./gradlew -q fatJar copyDockerResources
cd ./build/libs
echo "Building docker ..."
docker build -q -t test .
echo "Start application in container : "
docker run \
    -e PROPERTIES_LOCATION=/etc/config \
    -v ${DIR}/properties:/etc/config \
    test