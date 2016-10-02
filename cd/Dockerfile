FROM alpine:3.3

RUN apk --update add openjdk8-jre

COPY *.jar /srv/app.jar

WORKDIR /srv

ENTRYPOINT ["java", "-jar", "/srv/app.jar"]

EXPOSE 7777