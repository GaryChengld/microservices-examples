# Vert.x Circuit breaker example

This example demonstrates how to build aggregation microservice pattern with vert.x which uses vert.x discovery and circuit breaker.

In aggregation pattern, a microservice aggregates the results from other microservices. In this example, A calls B, C with circuit breakers and
 returns the aggregated result to the client. 
 
 ## Run the example locally
 
 First build the projects, with maven:
   
 ```
 mvn clean install
 ```
 
 The MicroServices are going to open the port: 9080(A), 9081(B) and 9082(C)
 
 Open 3 terminals, one for each microservice
 
```
cd service-A
java -jar target\circuit-breaker-vertx-service-A-fat.jar --cluster -conf src\conf\local.json
```
```
cd service-B
java -jar target\circuit-breaker-vertx-service-B-fat.jar --cluster -conf src\conf\local.json
```
```
cd service-C
java -jar target\circuit-breaker-vertx-service-C-fat.jar --cluster -conf src\conf\local.json
``` 

Once all microservices lunched, open a browser to `http://localhost:9080/serviceA/`, you should get
```
{
  "ServiceA result" : "Welcome to Service A",
  "ServiceC result" : "Welcome to Service C",
  "ServiceB result" : "Welcome to Service B"
}
```
in Browser.

When shutting down microservice B or C, it does not reply to the request anymore. The circuit breaker intercepts the error
and execute a fallback. If you restarts it, the output should be back to _normal_. This is because the circuit breaker tries periodically to reset its state and check whether or not things are back to _normal_.
