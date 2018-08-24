## Example of Reactive restful service build on vert.x

### APIs

|method|url|desc|
|:---|:---|:---|
|POST|http://localhost:8180/api/v1/movie/|Create a new movie record|
|PUT|http://localhost:8180/api/v1/movie/|Update a movie record|
|Delete|http://localhost:8180/api/v1/movie/:id|Delete a movie record|
|GET|http://localhost:8180/api/v1/movie/:id|Get a movie record by ID|
|GET|http://localhost:8180/api/v1/movie/imdb/:id|Get a movie record by Imdb ID|
|GET|http://localhost:8180/api/v1/movie/search/:keyword|Search movie by keyword|

```
If runing under docker, change localhost to docker machine ip address (default 192.168.99.100 in windows)
```

### Local 

#### Build
```
mvn clean package
```

#### Start
```
java -jar target\rest-vertx-fat.jar -conf src\conf\config.json
```

### Docker

#### Build
```
mvn clean package
./build.sh
```

#### Start
```
docker run -p 8180:8180 examples/rest/vertx
```
