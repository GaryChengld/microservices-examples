package io.examples.rest.vertx.https;

import io.examples.store.repository.RxProductRepository;
import io.examples.vertx.handler.PetHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;


/**
 * Main verticle of Greeting service
 *
 * @author Gary Cheng
 */
@Slf4j
public class MainVerticle extends AbstractVerticle {
    private static final String CONFIG_FILE = "src/conf/config.json";
    private static final String API_BASE_PATH = "/v1/pet";
    private static final String KEY_KEY_STORE = "keyStore";
    private static final String KEY_PATH = "path";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PORT = "port";

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.fileSystem().rxReadFile(CONFIG_FILE)
                .map(Buffer::toJsonObject)
                .map(json -> new DeploymentOptions().setConfig(json))
                .flatMap(options -> vertx.rxDeployVerticle(MainVerticle.class.getName(), options))
                .subscribe(id -> log.debug("MainVerticle deployed successfully with deployment ID {}", id),
                        ex -> {
                            log.error(ex.getLocalizedMessage());
                            vertx.close();
                        });
    }

    @Override
    public void start(Future<Void> startFuture) {
        log.debug("Starting greeting service...");
        PetHandler petHandler = PetHandler.create(vertx, RxProductRepository.getInstance());
        int port = this.config().getInteger(KEY_PORT, 8443);
        JsonObject keyStoreConfig = this.config().getJsonObject(KEY_KEY_STORE);
        Router router = Router.router(vertx);
        router.mountSubRouter(API_BASE_PATH, petHandler.router());
        HttpServerOptions options = new HttpServerOptions()
                .setSsl(true)
                .setKeyStoreOptions(new JksOptions()
                        .setPath(keyStoreConfig.getString(KEY_PATH))
                        .setPassword(keyStoreConfig.getString(KEY_PASSWORD)));
        vertx.createHttpServer(options)
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .doOnComplete(() -> log.debug("Pet restful service started on port {}", port))
                .subscribe(startFuture::complete, startFuture::fail);
    }
}
