package io.examples.vertx.handler;

import io.examples.common.ApiResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;


import static io.examples.common.HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
import static io.examples.common.HttpResponseCodes.SC_OK;

/**
 * Abstract Vert.x restful request handler
 *
 * @author Gary Cheng
 */
public class AbstractHandler {
    public <T> void buildResponse(RoutingContext context, T body) {
        String jsonString;
        if (body instanceof JsonObject) {
            jsonString = ((JsonObject) body).encode();
        } else if (body instanceof JsonArray) {
            jsonString = ((JsonArray) body).encode();
        } else {
            jsonString = JsonObject.mapFrom(body).encode();
        }
        context.response()
                .setStatusCode(SC_OK)
                .putHeader("Content-Type", "application/json")
                .end(jsonString);
    }

    public void exceptionResponse(RoutingContext context, Throwable throwable) {
        context.response()
                .setStatusCode(SC_INTERNAL_SERVER_ERROR)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(ApiResponse.error(9999, throwable.getLocalizedMessage())).encode());
    }

}
