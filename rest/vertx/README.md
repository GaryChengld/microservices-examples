## This is Reactive restful service example build on vert.x

## Local 

#### Build
```
mvn clean package
```

#### Start
```
cd service
java -jar target\rest-vertx-fat.jar -conf src\conf\config.json
```

## Docker

#### Build
```
mvn clean package
./build.sh
```

#### Start

```
docker run -p 8180:8180 examples/rest/vertx
```
