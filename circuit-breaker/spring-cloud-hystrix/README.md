# Circuit Breaker example on spring Cloud Eureka and Hystrix

This example demonstrates how to build Curcuit Breaker pattern with Spring Cloud Eureka and Hystrix.

  ## Run the example locally
 
 First build the projects, with maven:
   
 ```
 mvn clean install
 ```
 
The MicroServices are going to open the port: 8761(Eureka Service discovery), 9080(A), 9081(B) and 9082(C)
 
Open 4 terminals, one for each component

```
cd eureka-server
java -jar target\circuit-breaker-boot-eureka-server-0.1.0-SNAPSHOT.jar

```
 
```
cd service-A
java -jar target\circuit-breaker-boot-service-A-0.1.0-SNAPSHOT.jar
```

```
cd service-B
java -jar target\circuit-breaker-boot-service-B-0.1.0-SNAPSHOT.jar
```
```
cd service-C
java -jar target\circuit-breaker-boot-service-C-0.1.0-SNAPSHOT.jar
``` 

Once all microservices lunched, open a browser to `http://localhost:9080/serviceA/`, you should get
```
{"ServiceA result" : "Welcome to Service A","ServiceC result" : "Welcome to Service C","ServiceB result" : "Welcome to Service B"}
```
in Browser.

When shutting down microservice B or C, it does not reply to the request anymore. The circuit breaker intercepts the error and execute a fallback. The circuit breaker will open after failed 5 times, If you restarts it, the output should be back to _normal_. This is because the circuit breaker tries periodically to reset its state and check whether or not things are back to _normal_.
