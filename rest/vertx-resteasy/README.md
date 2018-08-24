## This example showing build JAX-RS restful service on Vert.x and Resteasy

[RESTEasy](https://resteasy.github.io/) is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java applications. It is a fully certified and portable implementation of the JAX-RS 2.1 specification, a JCP specification that provides a Java API for RESTful Web Services over the HTTP protocol.

Vert.x dosn't have Resteasy adapter in rxjava style for now , have to implement it by callback style libs.

###Build Hint

Every Resteasy provider jar has it's one javax.ws.rs.ext.Providers file under META-INF\services folder, to prevent maven overwrite it during build fat jar, need add this file to AppendingTransformer

```
<transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
    <resource>META-INF/services/javax.ws.rs.ext.Providers</resource>
</transformer>
```
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
