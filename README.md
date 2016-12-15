
# Template for microservices

This template standardizes a way of working with micro services.


Provides:

- jax-rs 2 endpoints
- cqrs setup using kafka

- prometheus metrics
- swagger-ui dist ("as is") 

- Jenkins jobdsl description
- kubernetes deployment scripts

- using properties file locally and a ConfigMap on PROD

# Prerequisites

## to run

- no prereq.

## for CD
- have docker
- have a jenkins installation
- have a kubernetes cluster
- have kubectl available in Jenkins
- configure .kube/config on Jenkins to allow kubectl to talk to the clustr
- have some job that seeds cd/dsl.groovy

# Customize

### build.gradle

Change group 'com.wijdemans' in build.gradle

### project.dsl.groovy

To remove sensitive data from the build set the following environment variables. 

Set JENKINS_GITHUB_CREDENTIALS to a reference id for valid credentials.
Set docker registry to valid repository.

(for example by setting these as global jenkins variables)

### property files

The property files are outside of the fatJar (in the ./properties directory)
The application expects the config files it needs to reside at the env variable PROPERTIES_LOCATION.

These values are set:
- via gradle job when running locally
- via -e PROPERTIES_LOCATION=/etc/config option when running docker locally (see docker.sh)
- via ConfigMap when running on the cluster (see deployment.yaml.template) 

# Testing web socket suport

## Using curl 

```
 curl -i -N -L \
 	-H "Connection: Upgrade" \
 	-H "Upgrade: websocket" \
    -H "Sec-WebSocket-Version: 13" \
    -H 'Sec-WebSocket-Key: key' \
 	http://localhost:7755/echo
```

## Cmd line test application
Simple go ws application: https://github.com/raphael/wsc

Be sure to specifiy ws:// as protocol:

```
wsc ws://localhost:7755/echo
```
