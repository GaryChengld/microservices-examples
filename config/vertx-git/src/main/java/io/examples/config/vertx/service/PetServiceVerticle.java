package io.examples.config.vertx.service;

import io.examples.store.repository.RxProductRepository;
import io.examples.vertx.handler.PetHandler;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.examples.common.HttpResponseCodes.SC_SERVICE_UNAVAILABLE;

/**
 * Pet service verticle
 *
 * @author Gary Cheng
 */
public class PetServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(PetServiceVerticle.class);
    private static final String API_BASE_PATH = "/v1/pet";
    private static final String KEY_PORT = "port";

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting PetServiceVerticle Verticle, config:{}", this.config().encode());
        int port = this.config().getInteger(KEY_PORT, 9081);
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
        PetHandler petHandler = PetHandler.create(vertx, RxProductRepository.getInstance());
        Router router = Router.router(vertx);
        router.mountSubRouter(API_BASE_PATH, petHandler.router());
        return router;
    }
}
