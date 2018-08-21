FROM java:8-jre

ENV SERVICE_APP_FILE target/rest-vertx-https-fat.jar

# Set the location of the services
ENV SERVICE_HOME /opt/services

EXPOSE 4443

COPY $SERVICE_APP_FILE $SERVICE_HOME/
COPY src/conf/config.json $SERVICE_HOME/
COPY server-keystore.jks $SERVICE_HOME/


WORKDIR $SERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar $SERVICE_APP_FILE -conf config.json"]
