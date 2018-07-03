package vertx.examples.discovery.consul;

import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.consul.ConsulClient;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

/**
 * The Service verticle on HTTP server and registered on consul server
 *
 * @author Gary Cheng
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);
    private static final String KEY_SERVICE_NAME = "ServiceName";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";

    private static final String HEALTH_CHECK_URL = "/healthCheck";
    private ConsulClient consulClient;
    private String serviceId;

    @Override
    public void start(Future<Void> startFuture) {
        JsonObject config = this.config();
        int port = config.getInteger(KEY_PORT);
        this.consulClient = ConsulClient.create(vertx);
        Router router = Router.router(vertx);
        router.get(HEALTH_CHECK_URL).handler(this::healthCheckHandler);
        router.route("/").handler(this::serviceHandler);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .doOnSuccess(s -> logger.debug("HTTP server started"))
                .flatMapCompletable(s -> this.registerService())
                .doOnComplete(() -> logger.debug("Register service completed"))
                .doOnError(startFuture::fail)
                .subscribe(startFuture::complete);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        logger.debug("stopping verticle");
        if (null != this.serviceId) {
            this.consulClient.rxDeregisterService(this.serviceId)
                    .doOnComplete(() -> logger.debug("Service de-registered"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Completable registerService() {
        logger.debug("Register service");
        JsonObject config = this.config();
        String serviceName = config.getString(KEY_SERVICE_NAME);
        String host = config.getString(KEY_HOST, "localhost");
        Integer port = config.getInteger(KEY_PORT, 8080);
        this.serviceId = UUID.randomUUID().toString();
        ServiceOptions options = new ServiceOptions().setName(serviceName)
                .setId(serviceId)
                .setTags(Arrays.asList("http-endpoint"))
                .setAddress(host)
                .setPort(port)
                .setCheckOptions(new CheckOptions()
                        .setHttp("http://" + host + ":" + port + HEALTH_CHECK_URL)
                        .setInterval("30s")
                        .setDeregisterAfter("30m"));
        return this.consulClient.rxRegisterService(options);
    }

    private void serviceHandler(RoutingContext context) {
        logger.debug("Received service request");
        context.response().putHeader("content-type", "application/json")
                .end(new JsonObject().put("message", "Welcome to MyService").encodePrettily());
    }

    private void healthCheckHandler(RoutingContext context) {
        logger.debug("Received health check request");
        context.response()
                .putHeader("content-type", "text/html")
                .end("<html><body><h1>My Service is working normally!</h1></body></html>");
    }
}
