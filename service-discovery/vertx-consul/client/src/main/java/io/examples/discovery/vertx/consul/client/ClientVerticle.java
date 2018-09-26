package io.examples.discovery.vertx.consul.client;

import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client verticle which invokes service
 *
 * @author Gary Cheng
 */
public class ClientVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ClientVerticle.class);
    private static final String SERVICE_NAME = "MyService";
    private ServiceDiscovery discovery;

    public static void main(String[] args) {
        JsonObject config = new JsonObject()
                .put("host", "127.0.0.1")
                .put("port", 8500)
                .put("dc", "dc1");

        Vertx.vertx().rxDeployVerticle(ClientVerticle.class.getName(), new DeploymentOptions().setConfig(config))
                .subscribe(id -> logger.debug("ClientVerticle deployed successfully"));
    }

    @Override
    public void start() {
        logger.debug("Starting client verticle");
        this.discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(this.config()));
        vertx.setPeriodic(3000, id -> this.findService().subscribe(this::sendRequest, ex -> logger.error(ex.getMessage())));
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
