package io.examples.apigateway.vertx.gateway;

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
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.examples.common.HttpResponseCodes.SC_SERVICE_UNAVAILABLE;

/**
 * API Gateway verticle
 *
 * @author Gary Cheng
 */
public class ApiGatewayVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayVerticle.class);
    private static final String KEY_PORT = "port";
    private ServiceDiscovery discovery;

    // Convenience method so you can run it in IDE
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(KEY_PORT, 9080);
        Vertx.rxClusteredVertx(new VertxOptions().setClustered(true))
                .flatMap(vertx -> vertx.rxDeployVerticle(ApiGatewayVerticle.class.getName(), new DeploymentOptions().setConfig(jsonObject)))
                .subscribe(id -> logger.debug("ApiGatewayVerticle deployed successfully with deployment ID {}", id),
                        t -> logger.error(t.getLocalizedMessage()));
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting ApiGatewayVerticle Verticle");
        int port = this.config().getInteger(KEY_PORT, 9080);
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
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    private Router router(Vertx vertx) {
        GatewayHandler handler = new GatewayHandler(this.discovery);
        Router router = Router.router(vertx);
        router.route().handler(this.corsHandler());
        ServiceDiscoveryRestEndpoint.create(router.getDelegate(), discovery.getDelegate());
        router.route("/v1/pet/*").handler(handler::handlePetRequest);
        router.route("/v1/review/*").handler(handler::handleReviewRequest);
        return router;
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
