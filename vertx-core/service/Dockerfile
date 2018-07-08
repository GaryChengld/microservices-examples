FROM java:8-jre

ENV SERVICE_APP_FILE target/vertx-core-discovery-service-fat.jar.jar

# Set the location of the services
ENV SERVICE_HOME /opt/services

EXPOSE 8082

COPY $SERVICE_APP_FILE $SERVICE_HOME/
COPY src/conf/docker.json $SERVICE_HOME/


WORKDIR $SERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar vertx-core-discovery-service-fat.jar -cluster -conf docker.json"]
