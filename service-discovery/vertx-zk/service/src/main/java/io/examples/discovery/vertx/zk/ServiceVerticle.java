package io.examples.discovery.vertx.zk;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Service verticle on HTTP server
 * This service is using Apache zookeeper as service discovery
 *
 * @author Gary Cheng
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);
    private static final int SC_SERVICE_UNAVAILABLE = 503;
    private static final String KEY_SERVICE = "service";
    private static final String KEY_ZOOKEEPER = "zookeeper";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";

    private ServiceDiscovery discovery;
    private Record publishedRecord;

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting Service Verticle");
        JsonObject serviceConfig = this.config().getJsonObject(KEY_SERVICE);
        String serviceName = serviceConfig.getString(KEY_NAME);
        String serviceHost = serviceConfig.getString(KEY_HOST, "localhost");
        Integer servicePort = serviceConfig.getInteger(KEY_PORT, 8080);

        this.discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(this.config().getJsonObject(KEY_ZOOKEEPER)));
        Router router = Router.router(vertx);
        ServiceDiscoveryRestEndpoint.create(router.getDelegate(), discovery.getDelegate());
        router.route("/").handler(this::serviceHandler);
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .subscribe(req -> {
                    logger.debug("Received service request");
                    req.resume();
                    router.accept(req);
                });
        server.rxListen(servicePort)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", servicePort))
                .flatMap(s -> this.publishService(serviceName, serviceHost, servicePort))
                .doAfterSuccess(r -> logger.debug("Service published"))
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        if (null != this.publishedRecord) {
            discovery.rxUnpublish(this.publishedRecord.getRegistration())
                    .doOnComplete(() -> logger.debug("Service unPublished"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Single<Record> publishService(String name, String host, Integer port) {
        return this.discovery.rxPublish(HttpEndpoint.createRecord(name, host, port, "/"))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private void serviceHandler(RoutingContext context) {
        context.response()
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", "Welcome to My Service").encodePrettily());
    }
}
