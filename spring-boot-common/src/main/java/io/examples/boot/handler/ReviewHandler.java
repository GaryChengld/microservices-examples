package io.examples.boot.handler;

import io.examples.common.ApiResponses;
import io.examples.review.domain.Review;
import io.examples.review.repository.FluxReviewRepository;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static io.examples.common.ApiResponses.MSG_DELETE_REVIEW_SUCCESS;
import static io.examples.common.ApiResponses.MSG_UPDATE_REVIEW_SUCCESS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Functional handler for review restful service
 *
 * @author Gary Cheng
 */
public class ReviewHandler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReviewHandler.class);
    private FluxReviewRepository repository;

    public ReviewHandler(FluxReviewRepository repository) {
        this.repository = repository;
    }

    /**
     * Return routing configuration of review handler
     *
     * @return RouterFunction
     */
    public RouterFunction<ServerResponse> getRouterFunction() {
        return nest(accept(APPLICATION_JSON),
                route(GET("/{id}"), this::byId)
                        .andRoute(GET("/findByProduct/{productId}"), this::byProduct)
                        .andRoute(POST("/"), this::add)
                        .andRoute(PUT("/{id}"), this::update)
                        .andRoute(DELETE("/{id}"), this::delete)
        );
    }

    private Mono<ServerResponse> byId(ServerRequest request) {
        logger.debug("Received find review by id request, id={}", request.pathVariable("id"));
        try {
            Integer id = Integer.valueOf(request.pathVariable("id"));
            return repository.getReviewById(id)
                    .flatMap(this::buildResponse)
                    .switchIfEmpty(this.reviewNotFoundResponse());
        } catch (NumberFormatException e) {
            return this.exceptionResponse(e);
        }
    }

    private Mono<ServerResponse> byProduct(ServerRequest request) {
        logger.debug("Received find review by product request, productId={}", request.pathVariable("productId"));
        try {
            Integer productId = Integer.valueOf(request.pathVariable("productId"));
            return ServerResponse.ok().
                    contentType(APPLICATION_JSON)
                    .body(repository.getReviewByProductId(productId), Review.class);
        } catch (NumberFormatException e) {
            return this.exceptionResponse(e);
        }
    }

    private Mono<ServerResponse> add(ServerRequest request) {
        logger.debug("Receiving add review request");
        return request.bodyToMono(Review.class)
                .flatMap(repository::addReview)
                .flatMap(this::buildResponse);
    }

    private Mono<ServerResponse> update(ServerRequest request) {
        logger.debug("Received update review request");
        try {
            Integer id = Integer.valueOf(request.pathVariable("id"));
            AtomicReference<Review> reviewRef = new AtomicReference<>();
            return request.bodyToMono(Review.class)
                    .flatMap(r -> {
                        r.setId(id);
                        reviewRef.set(r);
                        return repository.getReviewById(id);
                    })
                    .flatMap(product -> repository.updateReview(reviewRef.get()))
                    .map(updated -> MSG_UPDATE_REVIEW_SUCCESS)
                    .flatMap(this::buildResponse)
                    .switchIfEmpty(this.reviewNotFoundResponse());
        } catch (NumberFormatException e) {
            return this.exceptionResponse(e);
        }
    }

    private Mono<ServerResponse> delete(ServerRequest request) {
        logger.debug("Received delete review request");
        try {
            Integer id = Integer.valueOf(request.pathVariable("id"));
            return repository.getReviewById(id)
                    .flatMap(r -> repository.deleteReview(id))
                    .map(deleted -> MSG_DELETE_REVIEW_SUCCESS)
                    .flatMap(this::buildResponse)
                    .switchIfEmpty(this.reviewNotFoundResponse());
        } catch (NumberFormatException e) {
            return this.exceptionResponse(e);
        }
    }

    private Mono<ServerResponse> reviewNotFoundResponse() {
        return ServerResponse.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(fromObject(ApiResponses.ERR_REVIEW_NOT_FOUND));
    }
}
