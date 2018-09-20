package io.examples.circuitbreaker.vertx.service;

import io.reactivex.Single;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.circuitbreaker.CircuitBreaker;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.examples.common.HttpResponseCodes.SC_SERVICE_UNAVAILABLE;

/**
 * Verticle for service A which calls service B and C with circuit breaker
 *
 * @author Gary Cheng
 */
public class ServiceAVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAVerticle.class);
    private static final String KEY_NAME = "name";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";

    private static final String SERVICE_A_PATH = "/serviceA/";
    private static final String SERVICE_B_PATH = "/serviceB/";
    private static final String SERVICE_C_PATH = "/serviceC/";
    private static final String SERVICE_B_NAME = "ServiceB";
    private static final String SERVICE_C_NAME = "ServiceC";

    private ServiceDiscovery discovery;
    private Record publishedRecord;
    private Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    // Convenience method so you can run it in IDE
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(KEY_SERVICE, "ServiceA");
        jsonObject.put(KEY_HOST, "localhost");
        jsonObject.put(KEY_PORT, 9080);
        Vertx.rxClusteredVertx(new VertxOptions().setClustered(true))
                .flatMap(vertx -> vertx.rxDeployVerticle(ServiceAVerticle.class.getName(), new DeploymentOptions().setConfig(jsonObject)))
                .subscribe(id -> logger.debug("Service A Verticle deployed successfully with deployment ID {}", id),
                        t -> logger.error(t.getLocalizedMessage()));
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting Service A Verticle");
        int port = this.config().getInteger(KEY_PORT, 9080);
        this.discovery = ServiceDiscovery.create(vertx);
        Router router = this.router(vertx);
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .subscribe(req -> {
                    logger.debug("Service A received a HTTP request");
                    req.resume();
                    router.accept(req);
                });
        server.rxListen(port)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", s.actualPort()))
                .flatMap(s -> this.registerService(this.config()))
                .doAfterSuccess(r -> logger.debug("ServiceA published"))
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        this.circuitBreakerMap.values().forEach(CircuitBreaker::close);
        this.circuitBreakerMap.clear();
        if (null != this.publishedRecord) {
            discovery.rxUnpublish(this.publishedRecord.getRegistration())
                    .doOnComplete(() -> logger.debug("ServiceA unPublished"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Router router(Vertx vertx) {
        Router router = Router.router(vertx);
        ServiceDiscoveryRestEndpoint.create(router.getDelegate(), discovery.getDelegate());
        router.route(SERVICE_A_PATH).handler(this::handleService);
        return router;
    }

    private Single<Record> registerService(JsonObject config) {
        return this.discovery.rxPublish(this.createRecord(config))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private Record createRecord(JsonObject config) {
        return HttpEndpoint.createRecord(config.getString(KEY_SERVICE), config.getString(KEY_HOST),
                config.getInteger(KEY_PORT), SERVICE_A_PATH);
    }

    private void handleService(RoutingContext context) {
        JsonObject result = new JsonObject();
        result.put("ServiceA result", "Welcome to Service A");
        Single.merge(this.callServiceB(), this.callServiceC())
                .doAfterTerminate(() -> context.response().putHeader("content-type", "application/json").end(result.encodePrettily()))
                .subscribe(result::mergeIn);
    }

    private Single<JsonObject> callServiceB() {
        return callServiceWithFallback(SERVICE_B_NAME, SERVICE_B_PATH, this::fallbackServiceB);
    }

    private Single<JsonObject> callServiceC() {
        return callServiceWithFallback(SERVICE_C_NAME, SERVICE_C_PATH, this::fallbackServiceC);
    }

    private Single<JsonObject> callServiceWithFallback(String service, String uri, Function<Throwable, JsonObject> fallback) {
        logger.debug("Call Service with fallback, service:{}, uri:{}", service, uri);
        return this.getCircuitBreaker(service)
                .rxExecuteCommandWithFallback(future -> this.callService(service, uri).subscribe(future::complete, future::fail),
                        fallback::apply);
    }

    private JsonObject fallbackServiceB(Throwable throwable) {
        return new JsonObject().put("ServiceB result", throwable.getLocalizedMessage());
    }

    private JsonObject fallbackServiceC(Throwable throwable) {
        return new JsonObject().put("ServiceC result", throwable.getLocalizedMessage());
    }

    private Single<JsonObject> callService(String service, String uri) {
        return HttpEndpoint.rxGetWebClient(discovery, new JsonObject().put(KEY_NAME, service))
                .flatMap(webClient -> webClient.request(HttpMethod.GET, uri).rxSend())
                .map(HttpResponse::bodyAsJsonObject);
    }

    private CircuitBreaker getCircuitBreaker(String serviceName) {
        logger.debug("Get CircuitBreaker of service {}", serviceName);
        CircuitBreaker circuitBreaker = circuitBreakerMap.get(serviceName);
        if (null == circuitBreaker) {
            circuitBreaker = this.createCircuitBreaker(serviceName);
            this.circuitBreakerMap.put(serviceName, circuitBreaker);
        }
        return circuitBreaker;
    }

    private CircuitBreaker createCircuitBreaker(String serviceName) {
        logger.debug("Create CircuitBreaker for service {}", serviceName);
        String circuitBreakerName = serviceName + "-" + "circuit-breaker";
        CircuitBreakerOptions options = new CircuitBreakerOptions()
                .setMaxFailures(5)
                .setTimeout(5000)
                .setResetTimeout(10000)
                .setFallbackOnFailure(true);
        return CircuitBreaker.create(circuitBreakerName, vertx, options)
                .openHandler(v -> logger.debug("{} opened", circuitBreakerName))
                .halfOpenHandler(v -> logger.debug("{} half opened", circuitBreakerName));
    }
}
