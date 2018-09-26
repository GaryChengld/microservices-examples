# Vert.x using the Git to store externalized configuration

This example externalized configuration on GitHub Repository https://github.com/GaryChengld/microservices-examples-config-repo.git

## Pre-condition
Install GIT on local and add git command to path

## Run the example locally
 
Build with maven
   
 ```
 mvn clean install
 ```
 
Run service

```
java -jar target\config-vertx-git-fat.jar

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

