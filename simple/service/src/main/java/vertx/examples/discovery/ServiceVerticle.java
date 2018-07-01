package vertx.examples.discovery;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Service verticle on HTTP server
 *
 * @author Gary Cheng
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);
    private static final int SC_SERVICE_UNAVAILABLE = 503;
    private static final String KEY_SERVICE_NAME = "ServiceName";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private ServiceDiscovery discovery;
    private Record publishedRecord;

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting Service Verticle");
        JsonObject config = this.config();
        int port = config.getInteger(KEY_PORT);
        this.discovery = ServiceDiscovery.create(vertx);
        Router router = Router.router(vertx);
        ServiceDiscoveryRestEndpoint.create(router.getDelegate(), discovery.getDelegate());
        router.route("/").handler(this::serviceHandler);
        HttpServer server = vertx.createHttpServer();
        server.requestStream()
                .toFlowable()
                .map(HttpServerRequest::pause)
                .onBackpressureDrop(req -> req.response().setStatusCode(SC_SERVICE_UNAVAILABLE).end())
                .subscribe(req -> {
                    logger.debug("Received service request");
                    req.resume();
                    router.accept(req);
                });
        server.rxListen(port)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", port))
                .flatMap(s -> this.publishService())
                .doAfterSuccess(r -> logger.debug("Service published"))
                .subscribe(s -> startFuture.complete(), startFuture::fail);
    }

    @Override
    public void stop() {
        if (null != this.publishedRecord) {
            discovery.rxUnpublish(this.publishedRecord.getRegistration())
                    .doOnComplete(() -> this.publishedRecord = null)
                    .subscribe(() -> logger.debug("Service unPublished"));
        }
    }

    private Single<Record> publishService() {
        JsonObject config = this.config();
        String serviceName = config.getString(KEY_SERVICE_NAME);
        String host = config.getString(KEY_HOST, "localhost");
        Integer port = config.getInteger(KEY_PORT, 8080);
        return this.discovery.rxPublish(HttpEndpoint.createRecord(serviceName, host, port, "/"))
                .doOnSuccess(record -> this.publishedRecord = record);
    }

    private void serviceHandler(RoutingContext context) {
        context.response()
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", "Welcome to My Service").encodePrettily());
    }
}
