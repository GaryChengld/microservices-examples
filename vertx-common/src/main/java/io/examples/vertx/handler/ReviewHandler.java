package io.examples.vertx.handler;

import io.examples.review.domain.Review;
import io.examples.review.repository.RxReviewRepository;
import io.examples.store.ApiResponses;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.examples.common.HttpResponseCodes.SC_NOT_FOUND;

/**
 * Vert.x review request handler
 *
 * @author Gary Cheng
 */
public class ReviewHandler extends AbstractHandler {
    private static final Logger logger = LoggerFactory.getLogger(ReviewHandler.class);
    private Router router;
    private RxReviewRepository repository;

    private ReviewHandler(Vertx vertx, RxReviewRepository repository) {
        this.repository = repository;
        this.router = Router.router(vertx);
        router.get("/:id").handler(this::byId);
        router.get("/findByProduct/:productId").handler(this::byProduct);
        router.post("/").handler(this::add);
        router.put("/:id").handler(this::update);
        router.delete("/:id").handler(this::delete);
    }

    /**
     * Create API router for Pet Urls
     *
     * @param vertx the vertx instance
     * @return
     */
    public static ReviewHandler create(Vertx vertx, RxReviewRepository repository) {
        logger.debug("Creating ReviewHandler");
        return new ReviewHandler(vertx, repository);
    }

    public Router router() {
        return this.router;
    }

    private void byId(RoutingContext context) {
        logger.debug("Received find review by id request, id={}", context.request().getParam("id"));
        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            repository.getReviewById(id)
                    .subscribe(p -> this.buildResponse(context, p),
                            t -> this.exceptionResponse(context, t),
                            () -> this.notFoundResponse(context));
        } catch (NumberFormatException e) {
            this.exceptionResponse(context, e);
        }
    }

    private void byProduct(RoutingContext context) {
        logger.debug("Received find review by product request, productId={}", context.request().getParam("productId"));
        try {
            Integer productId = Integer.valueOf(context.request().getParam("productId"));
            repository.getReviewByProductId(productId)
                    .map(JsonObject::mapFrom)
                    .collect(JsonArray::new, JsonArray::add)
                    .subscribe(jsonArray -> this.buildResponse(context, jsonArray));
        } catch (NumberFormatException e) {
            this.exceptionResponse(context, e);
        }
    }

    private void add(RoutingContext context) {
        logger.debug("Receiving add review request");
        context.request().bodyHandler(buffer -> {
            logger.debug("Request body:{}", buffer.toString());
            Review review = buffer.toJsonObject().mapTo(Review.class);
            repository.addReview(review)
                    .subscribe(p -> this.buildResponse(context, p));
        });
    }

    private void update(RoutingContext context) {
        logger.debug("Received update review request");
        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            context.request().bodyHandler(buffer -> {
                logger.debug("Request body:{}", buffer.toString());
                Review review = buffer.toJsonObject().mapTo(Review.class);
                review.setId(id);
                repository.getReviewById(id)
                        .flatMap(r -> repository.updateReview(review).toMaybe())
                        .subscribe(b -> this.buildResponse(context, ApiResponses.MSG_UPDATE_SUCCESS),
                                t -> this.exceptionResponse(context, t),
                                () -> this.notFoundResponse(context));

            });
        } catch (NumberFormatException e) {
            this.exceptionResponse(context, e);
        }
    }

    private void delete(RoutingContext context) {
        logger.debug("Received delete review request");

        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            repository.getReviewById(id)
                    .flatMap(r -> repository.deleteReview(id).toMaybe())
                    .subscribe(b -> this.buildResponse(context, ApiResponses.MSG_UPDATE_SUCCESS),
                            t -> this.exceptionResponse(context, t),
                            () -> this.notFoundResponse(context));
        } catch (NumberFormatException e) {
            this.exceptionResponse(context, e);
        }
    }

    private void notFoundResponse(RoutingContext context) {
        context.response()
                .setStatusCode(SC_NOT_FOUND)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(ApiResponses.ERR_REVIEW_NOT_FOUND).encode());
    }
}
