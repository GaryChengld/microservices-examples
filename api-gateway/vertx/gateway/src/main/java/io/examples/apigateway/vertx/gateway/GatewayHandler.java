package io.examples.apigateway.vertx.gateway;

import io.examples.vertx.handler.AbstractHandler;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.HttpEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gary Cheng
 */
public class GatewayHandler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayVerticle.class);
    private static final String KEY_NAME = "name";
    private static final String PET_SERVICE_NAME = "pet";
    private static final String REVIEW_SERVICE_NAME = "review";
    private ServiceDiscovery discovery;

    GatewayHandler(ServiceDiscovery discovery) {
        this.discovery = discovery;
    }

    public void handlePetRequest(RoutingContext context) {
        logger.debug("Route pet request");
        this.dispatchRequest(PET_SERVICE_NAME, context);
    }

    public void handleReviewRequest(RoutingContext context) {
        logger.debug("Route review request");
        this.dispatchRequest(REVIEW_SERVICE_NAME, context);
    }

    private void dispatchRequest(String serviceName, RoutingContext context) {
        this.getWebClientByServiceName(serviceName)
                .flatMapCompletable(webClient -> this.exchange(webClient, context))
                .subscribe(() -> logger.debug("Request dispatched successfully"), t -> this.exceptionResponse(context, t));
    }

    private Single<WebClient> getWebClientByServiceName(String serviceName) {
        logger.debug("Get Web client by service name[{}]", serviceName);
        return HttpEndpoint.rxGetWebClient(discovery, new JsonObject().put(KEY_NAME, serviceName));
    }

    private Completable exchange(WebClient webClient, RoutingContext context) {
        return Completable.create(emitter ->
                this.sendRequest(webClient, context)
                        .subscribe(res -> {
                            if (!context.response().ended()) {
                                context.response().setStatusCode(res.statusCode());
                                res.headers().getDelegate().forEach(header -> context.response().putHeader(header.getKey(), header.getValue()));
                                context.response().end(res.body());
                            }
                            emitter.onComplete();
                        }, emitter::onError)
        );
    }

    private Single<HttpResponse<Buffer>> sendRequest(WebClient webClient, RoutingContext context) {
        HttpRequest<Buffer> targetRequest = webClient.request(context.request().method(), context.request().uri());
        context.request().headers().getDelegate().forEach(header -> targetRequest.putHeader(header.getKey(), header.getValue()));
        if (null != context.getBody()) {
            return targetRequest.rxSendBuffer(context.getBody());
        } else {
            return targetRequest.rxSend();
        }
    }
}
