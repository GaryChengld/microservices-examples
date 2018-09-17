package io.examples.rest.vertx.resteasy;

import io.examples.store.repository.RxProductRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main verticle of Pet service
 *
 * @author Gary Cheng
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);
    private static final String CONFIG_FILE = "src/conf/config.json";
    private static final String KEY_PORT = "port";

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject config = vertx.fileSystem().readFileBlocking(CONFIG_FILE).toJsonObject();
        DeploymentOptions options = new DeploymentOptions().setConfig(config);
        vertx.deployVerticle(MainVerticle.class.getName(), options, ar -> {
            if (ar.succeeded()) {
                logger.debug("MainVerticle deployed successfully with deployment ID {}", ar.result());
            } else {
                logger.error(ar.cause().getLocalizedMessage());
                vertx.close();
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting greeting service...");
        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();
        deployment.getRegistry().addSingletonResource(PetResource.create(RxProductRepository.getInstance()));

        int port = this.config().getInteger(KEY_PORT, 8080);
        vertx.createHttpServer()
                .requestHandler(new VertxRequestHandler(vertx, deployment))
                .listen(port, ar -> {
                    if (ar.succeeded()) {
                        logger.debug("Pet service started on port {}", ar.result().actualPort());
                        startFuture.complete();
                    } else {
                        startFuture.fail(ar.cause());
                    }
                });
    }
}
