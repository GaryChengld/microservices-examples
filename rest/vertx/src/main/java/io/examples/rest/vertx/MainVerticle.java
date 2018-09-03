package io.examples.rest.vertx;

import io.examples.petstore.repository.RxProductRepository;
import io.examples.petstore.repository.impl.RxProductRepositoryImpl;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.LoggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.examples.rest.vertx.common.ConfigKeys.KEY_PORT;
import static io.examples.rest.vertx.common.ConfigKeys.KEY_SERVICE;

/**
 * Main verticle of Movie service
 *
 * @author Gary Cheng
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private static final int SC_SERVICE_UNAVAILABLE = 503;
    private static final String CONFIG_FILE = "src/conf/config.json";
    private static final String API_BASE_PATH = "/v1/pet";

    // Convenience method so you can run it in IDE
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.fileSystem().rxReadFile(CONFIG_FILE)
                .map(Buffer::toJsonObject)
                .map(json -> new DeploymentOptions().setConfig(json))
                .flatMap(options -> vertx.rxDeployVerticle(MainVerticle.class.getName(), options))
                .subscribe(id -> logger.debug("MainVerticle deployed successfully with deployment ID {}", id),
                        ex -> {
                            logger.error(ex.getLocalizedMessage());
                            vertx.close();
                        });
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting main verticle...");
        this.startHttpServer(vertx, this.config().getJsonObject(KEY_SERVICE))
                .subscribe(startFuture::complete, startFuture::fail);
    }

    private Completable startHttpServer(Vertx vertx, JsonObject serviceConfig) {
        logger.debug("Starting Pet restful service...");
        RxProductRepository productRepository = new RxProductRepositoryImpl();
        int port = serviceConfig.getInteger(KEY_PORT, 8080);
        LoggerHandler loggerHandler = LoggerHandler.create();
        PetHandler petHandler = PetHandler.create(vertx, productRepository);
        Router router = Router.router(vertx);
        router.route().handler(loggerHandler);
        router.mountSubRouter(API_BASE_PATH, petHandler.router());
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureBuffer(128)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .observeOn(RxHelper.scheduler(vertx))
                .subscribe(req -> {
                    logger.debug("Received service request");
                    req.resume();
                    router.accept(req);
                });
        return server.rxListen(port)
                .toCompletable()
                .doOnComplete(() -> logger.debug("Pet restful service started on port {}", port));
    }
}
