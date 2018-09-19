package io.examples.circuitbreaker.vertx.service;

import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.examples.common.HttpResponseCodes.SC_SERVICE_UNAVAILABLE;

/**
 * Verticle for service B
 *
 * @author Gary Cheng
 */
public class ServiceBVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceBVerticle.class);
    private static final String KEY_SERVICE = "service";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String SERVICE_B_PATH = "/serviceB/";
    private ServiceDiscovery discovery;
    private Record publishedRecord;

    // Convenience method so you can run it in IDE
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(KEY_SERVICE, "ServiceB");
        jsonObject.put(KEY_HOST, "localhost");
        jsonObject.put(KEY_PORT, 9081);
        Vertx.rxClusteredVertx(new VertxOptions().setClustered(true))
                .flatMap(vertx -> vertx.rxDeployVerticle(ServiceBVerticle.class.getName(), new DeploymentOptions().setConfig(jsonObject)))
                .subscribe(id -> logger.debug("Service B Verticle deployed successfully with deployment ID {}", id),
                        t -> logger.error(t.getLocalizedMessage()));
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting Service B Verticle");
        int port = this.config().getInteger(KEY_PORT, 9081);
        this.discovery = ServiceDiscovery.create(vertx);
        Router router = this.router(vertx);
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .subscribe(req -> {
                    logger.debug("Service B received a HTTP request");
                    req.resume();
                    router.accept(req);
                });
        server.rxListen(port)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", s.actualPort()))
                .flatMap(s -> this.registerService(this.config()))
                .doAfterSuccess(r -> logger.debug("ServiceB published"))
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        if (null != this.publishedRecord) {
            discovery.rxUnpublish(this.publishedRecord.getRegistration())
                    .doOnComplete(() -> logger.debug("ServiceB unPublished"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        router.route(SERVICE_B_PATH).handler(this::handleService);
        return router;
    }

    private void handleService(RoutingContext context) {
        JsonObject result = new JsonObject();
        result.put("ServiceB result", "Welcome to Service B");
        context.response().putHeader("content-type", "application/json").end(result.encodePrettily());
    }

    private Single<Record> registerService(JsonObject config) {
        return this.discovery.rxPublish(this.createRecord(config))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private Record createRecord(JsonObject config) {
        return HttpEndpoint.createRecord(config.getString(KEY_SERVICE), config.getString(KEY_HOST),
                config.getInteger(KEY_PORT), "SERVICE_B_PATH");
    }
}
