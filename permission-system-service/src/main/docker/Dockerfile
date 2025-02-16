FROM registry.gitlab.com/myplayplanet/${registry.name}/service-image:latest

MAINTAINER MyPlayPlanet

COPY src/main/docker/start-server.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/start-server.sh && mkdir -p /data

EXPOSE 8080
EXPOSE 5544

VOLUME ["/data/configuration"]

ENV DEBUG false

COPY configuration /data/configuration
COPY target/${project.build.finalName}.jar /data/Service.jar

WORKDIR /data

CMD ["start-server.sh"]