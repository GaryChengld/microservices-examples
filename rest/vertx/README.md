## Example of Reactive restful service build on vert.x

[Vert.x](https://vertx.io/), known as one of the leading frameworks for performance, event-driven applications. It uses asynchronous programming principles which allows it to process a large number of concurrency using a small number of kernel threads. Asynchronous programming is a style promoting the ability to write non-blocking code. The platform stays responsive under heavy and varying load and is designed to follow [Reactive Manifesto](https://www.reactivemanifesto.org/) principles.


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
docker run -p 9080:9080 examples/rest/vertx
```
