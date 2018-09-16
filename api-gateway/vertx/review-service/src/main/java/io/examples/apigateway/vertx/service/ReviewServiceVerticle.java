package io.examples.apigateway.vertx.service;

import io.examples.review.repository.RxReviewRepository;
import io.examples.vertx.handler.ReviewHandler;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static io.examples.common.HttpResponseCodes.SC_SERVICE_UNAVAILABLE;

/**
 * Review Service verticle
 *
 * @author Gary Cheng
 */
public class ReviewServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceVerticle.class);
    private static final String API_BASE_PATH = "/v1/review";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private ServiceDiscovery discovery;
    private Record publishedRecord;


    // Convenience method so you can run it in IDE
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(KEY_SERVICE, "review");
        jsonObject.put(KEY_HOST, "localhost");
        jsonObject.put(KEY_PORT, 9082);
        Vertx.rxClusteredVertx(new VertxOptions().setClustered(true))
                .flatMap(vertx -> vertx.rxDeployVerticle(ReviewServiceVerticle.class.getName(), new DeploymentOptions().setConfig(jsonObject)))
                .subscribe(id -> logger.debug("PetServiceVerticle deployed successfully with deployment ID {}", id),
                        t -> logger.error(t.getLocalizedMessage()));
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting ReviewServiceVerticle Verticle");
        int port = this.config().getInteger(KEY_PORT, 9081);
        this.discovery = ServiceDiscovery.create(vertx);
        Router router = this.router(vertx);
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .subscribe(req -> {
                    logger.debug("Received HTTP request");
                    req.resume();
                    router.accept(req);
                });
        server.rxListen(port)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", s.actualPort()))
                .flatMap(s -> this.registerService(this.config()))
                .doAfterSuccess(r -> logger.debug("Review Service published"))
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        if (null != this.publishedRecord) {
            discovery.rxUnpublish(this.publishedRecord.getRegistration())
                    .doOnComplete(() -> logger.debug("Review Service unPublished"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Router router(Vertx vertx) {
        ReviewHandler reviewHandler = ReviewHandler.create(vertx, RxReviewRepository.getInstance());
        Router router = Router.router(vertx);
        router.route().handler(this.corsHandler());
        router.mountSubRouter(API_BASE_PATH, reviewHandler.router());
        return router;
    }

    private Single<Record> registerService(JsonObject config) {
        return this.discovery.rxPublish(this.createRecord(config))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private Record createRecord(JsonObject config) {
        return HttpEndpoint.createRecord(config.getString(KEY_SERVICE), config.getString(KEY_HOST),
                config.getInteger(KEY_PORT), API_BASE_PATH);
    }

    private CorsHandler corsHandler() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        CorsHandler corsHandler = CorsHandler.create("*").allowedHeaders(allowedHeaders);
        for (HttpMethod method : HttpMethod.values()) {
            corsHandler.allowedMethod(method);
        }
        return corsHandler;
    }
}
