package io.examples.boot.handler;

import io.examples.common.ApiResponse;
import io.examples.common.ApiResponses;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static io.examples.common.HttpResponseCodes.SC_INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author Gary Cheng
 */
public abstract class AbstractHandler {
    <T> Mono<ServerResponse> buildResponse(T body) {
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(body));
    }

    Mono<ServerResponse> exceptionResponse(Throwable throwable) {
        return ServerResponse.status(SC_INTERNAL_SERVER_ERROR)
                .contentType(APPLICATION_JSON)
                .body(fromObject(ApiResponse.error(9999, throwable.getLocalizedMessage())));
    }
}
