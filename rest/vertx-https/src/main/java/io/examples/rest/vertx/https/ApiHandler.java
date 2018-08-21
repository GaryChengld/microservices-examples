package io.examples.rest.vertx.https;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gary Cheng
 */
@Slf4j
public class ApiHandler {
    private Router router;

    private ApiHandler(Vertx vertx) {
        Router router = Router.router(vertx);
        router.get("/greetings").handler(this::greeting);
        router.get("/greetings/:name").handler(this::greetingWithName);
        this.router = router;
    }

    /**
     * Create API router for Movie Urls
     *
     * @param vertx the vertx instance
     * @return
     */
    public static ApiHandler apiHandler(Vertx vertx) {
        log.debug("Creating ApiHandler");
        return new ApiHandler(vertx);
    }

    public Router router() {
        return router;
    }

    private void greeting(RoutingContext context) {
        log.debug("Received greeting request");
        Greeting greeting = new Greeting("Hello World");
        this.handleGreetingResponse(context, greeting);
        log.debug("Handling greeting request completed");
    }

    private void greetingWithName(RoutingContext context) {
        log.debug("Received greetingWithName request");
        String name = context.request().getParam("name");
        Greeting greeting = new Greeting("Hello " + name);
        this.handleGreetingResponse(context, greeting);
        log.debug("Handling greetingWithName request completed");
    }

    private void handleGreetingResponse(RoutingContext context, Greeting greeting) {
        context.response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(greeting).encodePrettily());
    }
}
