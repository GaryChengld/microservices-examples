FROM java:8-jre

ENV SERVICE_APP_FILE target/rest-vertx-fat.jar

# Set the location of the services
ENV SERVICE_HOME /opt/services

EXPOSE 8180

COPY $SERVICE_APP_FILE $SERVICE_HOME/
COPY src/conf/config.json $SERVICE_HOME/


WORKDIR $SERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar rest-vertx-fat.jar -conf config.json"]
