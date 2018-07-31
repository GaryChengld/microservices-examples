package io.examples.rest.vertx;

import io.examples.rest.vertx.repository.MovieRepository;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.examples.rest.vertx.common.ConfigKeys.*;

/**
 * Main verticle of Movie service
 *
 * @author Gary Cheng
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private static final String CONFIG_FILE = "src/conf/config.json";
    private static final String API_BASE_PATH = "/api/v1";

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
        this.initApp();
        MovieRepository.create(vertx, this.config().getJsonObject(KEY_DATABASE))
                .flatMap(repo -> this.startHttpServer(vertx, this.config().getJsonObject(KEY_SERVICE), repo))
                .subscribe(server -> startFuture.complete(), startFuture::fail);
    }

    private void initApp() {
        Json.mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        Json.prettyMapper.disable(WRITE_DATES_AS_TIMESTAMPS);
    }

    private Single<HttpServer> startHttpServer(Vertx vertx, JsonObject serviceConfig, MovieRepository movieRepository) {
        logger.debug("Starting Movie HttpServer...");
        int port = serviceConfig.getInteger(KEY_PORT, 8080);
        Router router = Router.router(vertx);
        router.mountSubRouter(API_BASE_PATH, ApiHandler.createApiRouter(vertx, movieRepository));
        return vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .doAfterSuccess(server -> logger.debug("Movie http service started on port {}", port));
    }
}
