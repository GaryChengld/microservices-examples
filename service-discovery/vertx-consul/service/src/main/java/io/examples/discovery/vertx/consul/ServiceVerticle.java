package io.examples.discovery.vertx.consul;

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
 * The Service verticle on HTTP server and registered on consul server
 *
 * @author Gary Cheng
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);
    private static final int SC_SERVICE_UNAVAILABLE = 503;
    private static final String KEY_SERVICE = "service";
    private static final String KEY_DISCOVERY = "discovery";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_ROOT = "root";
    private static final String KEY_METADATA = "metadata";

    private ServiceDiscovery discovery;
    private Record publishedRecord;

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting verticle");
        JsonObject serviceConfig = this.config().getJsonObject(KEY_SERVICE);
        JsonObject discoveryConfig = this.config().getJsonObject(KEY_DISCOVERY, new JsonObject());
        Integer servicePort = serviceConfig.getInteger(KEY_PORT, 8080);
        this.discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(discoveryConfig));
        Router router = Router.router(vertx);

        ServiceDiscoveryRestEndpoint.create(router.getDelegate(), discovery.getDelegate());
        router.get("/healthCheck").handler(this::healthCheckHandler);
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
                .flatMap(s -> this.registerService(serviceConfig))
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

    private Single<Record> registerService(JsonObject serviceConfig) {
        return this.discovery.rxPublish(this.createRecord(serviceConfig))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private Record createRecord(JsonObject serviceConfig) {
        return HttpEndpoint.createRecord(serviceConfig.getString(KEY_NAME), serviceConfig.getString(KEY_HOST),
                serviceConfig.getInteger(KEY_PORT), serviceConfig.getString(KEY_ROOT),
                serviceConfig.getJsonObject(KEY_METADATA, new JsonObject()));
    }

    private void serviceHandler(RoutingContext context) {
        logger.debug("Received service request");
        context.response().putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", "Welcome to MyService").encodePrettily());
    }

    private void healthCheckHandler(RoutingContext context) {
        logger.debug("Received health check request");
        context.response()
                .putHeader("content-type", "text/plain")
                .end("UP");
    }
}