package vertx.examples.discovery.client;

import io.reactivex.Single;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Sample class which find service in ServiceDiscovery and call service
 *
 * @author Gary Cheng
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String SERVICE_NAME = "MyService";
    private ServiceDiscovery discovery;

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        Vertx.rxClusteredVertx(new VertxOptions().setClustered(true)).subscribe(vertx -> {
            this.discovery = ServiceDiscovery.create(vertx);
            vertx.setPeriodic(3000, id -> this.findService().subscribe(this::sendRequest, ex -> logger.error(ex.getMessage())));
        });
    }

    private Single<HttpClient> findService() {
        return HttpEndpoint.rxGetClient(this.discovery, new JsonObject().put("name", SERVICE_NAME));
    }

    private void sendRequest(HttpClient httpClient) {
        httpClient.getNow("/", resp -> {
            logger.debug("Got service response {}", resp.statusCode());
            resp.bodyHandler(body -> logger.debug("body:{}", body.toString()));
        });
    }
}
