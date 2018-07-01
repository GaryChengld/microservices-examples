FROM java:8-jre

ENV SERVICE_APP_FILE target/simple-discovery-service-0.1.0-SNAPSHOT-fat.jar

# Set the location of the services
ENV SERVICE_HOME /opt/services

EXPOSE 8082

COPY $SERVICE_APP_FILE $SERVICE_HOME/
COPY src/conf/docker.json $SERVICE_HOME/


WORKDIR $SERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar simple-discovery-service-0.1.0-SNAPSHOT-fat.jar -cluster -conf docker.json"]
