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
        shell("""
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

                   sed "s~{{DOCKER_IMAGE}}~\${DOCKER_IMAGE}~"  ./cd/rc.yaml.template > ./cd/rc.yaml

                   ~/kubectl apply -f ./cd/rc.yaml
                   ~/kubectl apply -f ./cd/service.yaml
              fi
              """.stripIndent())
    }
}

