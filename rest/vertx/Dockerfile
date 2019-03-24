FROM openjdk:8-jre-alpine

ENV SERVICE_APP_FILE target/rest-vertx-fat.jar

ENV SERVICE_HOME /opt/services

EXPOSE 9080

COPY $SERVICE_APP_FILE $SERVICE_HOME/
COPY src/conf/config.json $SERVICE_HOME/


WORKDIR $SERVICE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar rest-vertx-fat.jar -conf config.json"]
