
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

Set JENKINS_GITHUB_CREDENTIALS to a reference id for valid credentials.
Set docker registry to valid repository.

(for example by setting these as global jenkins variables)