
# Template for microservices

This template standardizes a way of working with micro services.


Provides:

- jax-rs 2 endpoints
- cqrs setup using kafka

- prometheus metrics
- swagger-ui dist ("as is") 

- Jenkins jobdsl description
- kubernetes deployment scripts


TODO :
- build properties
- runtime properties

# Prerequisites

## to run

- no prereq.

## for CD
- have docker
- have a jenkins installation
- have a kubernetes cluster
- have kubectl available in Jenkins

# Customize

### build.gradle

Change group 'com.wijdemans' in build.gradle

### project.dsl.groovy

To remove sensitive data from the build set the following environment variables. 

set JENKINS_GITHUB_CREDENTIALS to valid credentials:

```
export JENKINS_GITHUB_CREDENTIALS='****************************'
```

set docker registry to valid repository

```
export JENKINS_DOCKER_REGISTRY="registry.example.com"
```