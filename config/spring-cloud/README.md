# Externalized configuration by spring config server

This example externalized configuration on GitHub Repository https://github.com/GaryChengld/microservices-examples-config-repo.git

## Run the example locally
 
Build with maven
   
 ```
 mvn clean install
 ```
 
The MicroServices are going to open the port: 8761(Eureka Service discovery), 8888(Config server), and 9081(Pet service)
 
Open 3 terminals, one for each component

```
cd eureka-server
java -jar target\config-boot-eureka-server-0.1.0-SNAPSHOT.jar

```
 
```
cd config-server-git
java -jar target\config-boot-config-server-git-A-0.1.0-SNAPSHOT.jar
```
```
a -jar target\circuit-breaker-boot-service-B-0.1.0-SNAPSHOT.jar
```
```
cd pet-service
java -jar target\config-boot-pet-service-0.1.0-SNAPSHOT.jar
``` 

Once all services lunched, below resources will be enabled on localhost:9081

|method|url|desc|
|:---|:---|:---|
|GET|/v1/pet|Get all pets|
|GET|/v1/pet/{id}|Find pet by id|
|GET|/v1/pet/findByCategory/{category}|Find pets by category|
|POST|/v1/pet|Add a new pet|
|PUT|/v1/pet/{id}|Update a pet|
|Delete|/v1/pet/{id}|Delete a pet|

