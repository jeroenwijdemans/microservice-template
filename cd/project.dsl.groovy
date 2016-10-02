CREDNTIALS=System.getenv("JENKINS_GITHUB_CREDENTIALS")
REGISTRY=System.getenv("JENKINS_DOCKER_REGISTRY")
NAMESPACE="test"
NAME="template"

job("REST_TEMPLATE") {
    description "rest API template"
    scm {
        git {
            remote {
                github('microservice-template', 'ssh')
                credentials(CREDNTIALS)
            }
        }
    }
    steps {
        shell("./gradlew fatJar")
        shell("""
              VERSION=`./gradew version`
              DOCKER_IMAGE=${REGISTRY}/${NAMESPACE}/${NAME}:${VERSION}
              docker build -t ${DOCKER_IMAGE} .
              if [ \$? -ne 0 ]; then
                  echo "Failed building line client"
                  exit 1
              else
                  echo "Build succesfully - pushing to registry"
                  docker push ${DOCKER_IMAGE}

                   echo 'Start deployment ... '

                   sed -e 's/{{DOCKER_IMAGE}}/'"${DOCKER_IMAGE}"'/g' ./cd/rc.yaml.template > ./cd/rc.yaml

                   kubectl create -f ./cd/rc.yaml
                   kubectl create -f ./cd/service.yaml
              fi
              """.stripIndent())
    }
}

