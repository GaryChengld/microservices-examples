package io.examples.rest.vertx.resteasy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;


/**
 * Main verticle of Greeting service
 *
 * @author Gary Cheng
 */
@Slf4j
public class MainVerticle extends AbstractVerticle {
    private static final String CONFIG_FILE = "src/conf/config.json";
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
        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();
        deployment.getRegistry().addPerInstanceResource(GreetingResource.class);

        int port = this.config().getInteger(KEY_PORT, 8080);
        vertx.createHttpServer()
                .requestHandler(new VertxRequestHandler(vertx, deployment))
                .listen(port, ar -> {
                    if (ar.succeeded()) {
                        log.debug("Greeting service started on port {}", port);
                        startFuture.complete();
                    } else {
                        startFuture.fail(ar.cause());
                    }
                });
    }
}
