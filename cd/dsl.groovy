job("REST_TEMPLATE") {
    description "rest API template"
    scm {
        git {
            remote {
                github('jeroenwijdemans/microservice-template', 'ssh')
                credentials(JENKINS_GITHUB_CREDENTIALS)
            }
        }
    }
    steps {
        shell("./gradlew clean fatJar copyDockerResources")
        shell("./gradlew dep > dependencies.txt && ./gradlew dependencyCheck --info")
        shell("""
              set -e
              DIR="\$( cd "\$( dirname "\${BASH_SOURCE[0]}" )" && pwd )"

              NAMESPACE="test"
              NAME="template"

              VERSION=`./gradlew -q version`
              DOCKER_IMAGE="\$JENKINS_DOCKER_REGISTRY/\${NAMESPACE}/\${NAME}:\${VERSION}"

              cd ./build/libs
              docker build -t \${DOCKER_IMAGE} .
              if [ \$? -ne 0 ]; then
                  echo "Failed building line client"
                  exit 1
              else
                  echo "Build succesfully - pushing to registry"
                  docker push \${DOCKER_IMAGE}

                   cd \$DIR
                   echo 'Start deployment ... '

                   sed "s~{{DOCKER_IMAGE}}~\${DOCKER_IMAGE}~"  ./cd/deployment.yaml.template > ./cd/deployment.yaml

                   ~/kubectl apply -f ./cd/config.yaml
                   # all files in the properties directory will be added
                   ~/kubectl apply configmap config-service-template --from-file=properties/prod
                   ~/kubectl apply -f ./cd/deployment.yaml
                   ~/kubectl apply -f ./cd/service.yaml
                   ~/kubectl apply -f ./cd/ingress.yaml
              fi
              """.stripIndent())
    }
}

