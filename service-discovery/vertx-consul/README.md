## This is the vert.x Service Discovery example using Consul as backend

#### Maven Build

```
mvn clean package
```

#### run locally
1. Start consul
```
consul agent -data-dir=\tmp\consul -dev
```
2. Start service
```
cd service
java -jar service\target\vertx-consul-discovery-service-fat.jar -conf service\src\conf\local.json
```
3. Start client
```
cd client
java -jar service\target\vertx-consul-discovery-service-fat.jar -conf service\src\conf\local.json
```
