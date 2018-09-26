package io.examples.config.vertx.service;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gary Cheng
 */
public class MainVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(PetServiceVerticle.class);

    public static void main(String[] args) {
        Vertx.vertx().rxDeployVerticle(MainVerticle.class.getName())
                .subscribe(id -> logger.debug("MainVerticle deployed successfully with deployment ID {}", id),
                        t -> logger.error(t.getLocalizedMessage()));
    }

    @Override
    public void start(Future<Void> startFuture) {
        ConfigStoreOptions git = new ConfigStoreOptions()
                .setType("git")
                .setConfig(new JsonObject()
                        .put("url", "https://github.com/GaryChengld/microservices-examples-config-repo.git")
                        .put("path", "local")
                        .put("filesets",
                                new JsonArray().add(new JsonObject().put("pattern", "vertx-pet.json"))));
        ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(git));
        retriever.rxGetConfig().flatMap(config -> vertx.rxDeployVerticle(PetServiceVerticle.class.getName(), new DeploymentOptions().setConfig(config)))
                .subscribe(id -> startFuture.complete(),
                        startFuture::fail);
    }
}
