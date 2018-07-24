## This is the vert.x Service Discovery example using Apache Zookeeper as backend

#### Maven Build

```
mvn clean package
```

#### run locally
1. Run ZooKeeper in a standalone mode as it requires minimal configuration
2. Start service
```
cd service
java -jar service\target\vertx-zk-discovery-service-fat.jar -conf service\src\conf\local.json
```
3. Start client
```
cd client
java -jar service\target\vertx-zk-discovery-service-fat.jar -conf service\src\conf\zookeeper.json
```
