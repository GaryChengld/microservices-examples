package io.examples.discovery.vertx.consul.client;

import io.reactivex.Single;
import io.vertx.ext.consul.ServiceEntry;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.consul.ConsulClient;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * The client class which find service from consul and call the service
 *
 * @author Gary Cheng
 */
public class Client {
    private static final Logger logger = getLogger(Client.class);
    private static final String SERVICE_NAME = "MyService";

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        Vertx vertx = Vertx.vertx();
        ConsulClient consulClient = ConsulClient.create(vertx);
        vertx.setPeriodic(3000, id -> this.findService(consulClient)
                .subscribe(entry -> this.sendRequest(vertx, entry), ex -> logger.error(ex.getMessage())));
    }

    private Single<ServiceEntry> findService(ConsulClient consulClient) {
        return Single.create(emitter ->
                consulClient.rxHealthServiceNodes(SERVICE_NAME, true)
                        .subscribe(entryList -> {
                            if (!entryList.getList().isEmpty()) {
                                emitter.onSuccess(entryList.getList().get(0));
                            } else {
                                emitter.onError(new RuntimeException("Service not found"));
                            }
                        }, emitter::onError));
    }

    private void sendRequest(Vertx vertx, ServiceEntry entry) {
        vertx.createHttpClient().getNow(entry.getService().getPort(), entry.getService().getAddress(), "/", resp -> {
            logger.debug("Got service response {}", resp.statusCode());
            resp.bodyHandler(body -> logger.debug("body:{}", body.toString()));
        });
    }
}