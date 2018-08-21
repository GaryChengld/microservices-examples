## This is Reactive restful service example build on vert.x

### Local 

#### Build
```
mvn clean package
```

#### Start
```
java -jar target\rest-vertx-https-fat.jar -conf src\conf\config.json
```

### Docker

#### Build
```
mvn clean package
./build.sh
```

#### Start
```
docker run -p 4443:4443 examples/rest/vertx-https
```
