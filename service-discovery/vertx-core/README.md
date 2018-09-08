## This is the Service Discovery example implemented by Vert.x internal library

### Build

#### Maven Build

```
mvn clean package
```

#### Docker Build
If docker is running on Linux, change host to 127.0.0.1 in service\src\conf\docker.json
```
./build.sh
```

### Run

#### run Service locally
```
java -jar service\target\vertx-core-discovery-service-fat.jar --cluster -conf service\src\conf\local.json
```

#### Run service in docker container
```
docker run -p 8082:8082 service-discovery-simple/service
```
#### Run Client
```
java -jar client\target\simple-discovery-client-0.1.0-SNAPSHOT-fat.jar
```

