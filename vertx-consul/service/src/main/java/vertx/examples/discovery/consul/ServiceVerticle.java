package vertx.examples.discovery.consul;

import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;
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
    private static final int SC_SERVICE_UNAVAILABLE = 503;
    private static final String KEY_SERVICE = "service";
    private static final String KEY_CONSUL = "consul";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_HEALTH_CHECK_URL = "healthCheck";

    private ConsulClient consulClient;
    private String registeredServiceId;

    @Override
    public void start(Future<Void> startFuture) {
        logger.debug("Starting verticle");
        JsonObject serviceConfig = this.config().getJsonObject(KEY_SERVICE);
        JsonObject consulConfig = this.config().getJsonObject(KEY_CONSUL);

        ConsulClientOptions options = new ConsulClientOptions()
                .setHost(consulConfig.getString(KEY_HOST, "127.0.0.1"))
                .setPort(consulConfig.getInteger(KEY_PORT, 8500));
        this.consulClient = ConsulClient.create(vertx, options);

        String serviceName = serviceConfig.getString(KEY_NAME);
        String serviceHost = serviceConfig.getString(KEY_HOST, "localhost");
        Integer servicePort = serviceConfig.getInteger(KEY_PORT, 8080);
        String healthCheckURL = serviceConfig.getString(KEY_HEALTH_CHECK_URL);

        Router router = Router.router(vertx);
        router.get(healthCheckURL).handler(this::healthCheckHandler);
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
        server.rxListen(servicePort)
                .doAfterSuccess(s -> logger.debug("http server started on port {}", servicePort))
                .flatMapCompletable(s -> this.registerService(serviceName, serviceHost, servicePort, healthCheckURL))
                .doOnComplete(() -> logger.debug("Service published"))
                .subscribe(startFuture::complete, startFuture::fail);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        logger.debug("stopping verticle");
        if (null != this.registeredServiceId) {
            this.consulClient.rxDeregisterService(this.registeredServiceId)
                    .doOnComplete(() -> logger.debug("Service de-registered"))
                    .subscribe(stopFuture::complete);
        }
    }

    private Completable registerService(String name, String host, Integer port, String healthCheck) {
        logger.debug("Register service");
        String serviceId = UUID.randomUUID().toString();
        ServiceOptions options = new ServiceOptions().setName(name)
                .setId(serviceId)
                .setTags(Arrays.asList("http-endpoint"))
                .setAddress(host)
                .setPort(port)
                .setCheckOptions(new CheckOptions()
                        .setHttp("http://" + host + ":" + port + healthCheck)
                        .setInterval("30s")
                        .setDeregisterAfter("30m"));
        return this.consulClient.rxRegisterService(options)
                .doOnComplete(() -> this.registeredServiceId = serviceId);
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