FROM alpine:3.3

RUN apk --update add openjdk8-jre

COPY *.jar /srv/app.jar

WORKDIR /srv

# location of the config files
ENV     PROPERTIES_LOCATION=/etc/config
VOLUME  /etc/config

ENTRYPOINT ["java", "-jar", "/srv/app.jar"]

EXPOSE 7777