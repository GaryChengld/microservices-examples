package io.examples.rest.vertx;

import io.examples.common.ApiResponse;
import io.examples.petstore.ApiResponses;
import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.RxProductRepository;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.examples.rest.vertx.common.HttpResponseCodes.*;

/**
 * @author Gary Cheng
 */
public class PetHandler {
    private static final Logger logger = LoggerFactory.getLogger(PetHandler.class);
    private Router router;

    private RxProductRepository productRepository;

    private PetHandler(Vertx vertx, RxProductRepository productRepository) {
        this.productRepository = productRepository;
        this.router = Router.router(vertx);
        router.get("/").handler(this::all);
        router.get("/:id").handler(this::byId);
        router.get("/findByCategory/:category").handler(this::byCategory);
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
    public static PetHandler create(Vertx vertx, RxProductRepository productRepository) {
        logger.debug("Creating PetHandler");
        return new PetHandler(vertx, productRepository);
    }

    public Router router() {
        return this.router;
    }

    private void all(RoutingContext context) {
        logger.debug("Received all request");
        productRepository.getProducts()
                .map(JsonObject::mapFrom)
                .collect(JsonArray::new, JsonArray::add)
                .subscribe(jsonArray -> this.buildResponse(context, jsonArray));
    }

    private void byId(RoutingContext context) {
        logger.debug("Received byId request, id={}", context.request().getParam("id"));
        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            productRepository.getProductById(id)
                    .subscribe(p -> this.buildResponse(context, p),
                            t -> this.exceptionResponse(context, t),
                            () -> this.petNotFoundResponse(context));
        } catch (NumberFormatException e) {
            this.petNotFoundResponse(context);
        }
    }

    private void byCategory(RoutingContext context) {
        logger.debug("Received getMovieByImdbId request");
        String category = context.request().getParam("category");
        productRepository.getProductsByCategory(category)
                .map(JsonObject::mapFrom)
                .collect(JsonArray::new, JsonArray::add)
                .subscribe(jsonArray -> this.buildResponse(context, jsonArray));
    }

    private void add(RoutingContext context) {
        logger.debug("Receiving add request");
        context.request().bodyHandler(buffer -> {
            logger.debug("Request body:{}", buffer.toString());
            Product product = buffer.toJsonObject().mapTo(Product.class);
            productRepository.addProduct(product)
                    .subscribe(p -> this.buildResponse(context, p));
        });
    }

    private void update(RoutingContext context) {
        logger.debug("Received update request");
        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            context.request().bodyHandler(buffer -> {
                logger.debug("Request body:{}", buffer.toString());
                Product product = buffer.toJsonObject().mapTo(Product.class);
                product.setId(id);
                productRepository.getProductById(id)
                        .flatMap(p -> productRepository.updateProduct(product).toMaybe())
                        .subscribe(b -> this.buildResponse(context, ApiResponses.MSG_UPDATE_SUCCESS),
                                t -> this.exceptionResponse(context, t),
                                () -> this.petNotFoundResponse(context));

            });
        } catch (NumberFormatException e) {
            this.petNotFoundResponse(context);
        }
    }

    private void delete(RoutingContext context) {
        logger.debug("Received delete request");

        try {
            Integer id = Integer.valueOf(context.request().getParam("id"));
            productRepository.getProductById(id)
                    .flatMap(p -> productRepository.deleteProduct(id).toMaybe())
                    .subscribe(b -> this.buildResponse(context, ApiResponses.MSG_UPDATE_SUCCESS),
                            t -> this.exceptionResponse(context, t),
                            () -> this.petNotFoundResponse(context));
        } catch (NumberFormatException e) {
            this.petNotFoundResponse(context);
        }
    }


    private <T> void buildResponse(RoutingContext context, T body) {
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

    private void petNotFoundResponse(RoutingContext context) {
        context.response()
                .setStatusCode(SC_NOT_FOUND)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(ApiResponses.ERR_PET_NOT_FOUND).encode());
    }

    private void exceptionResponse(RoutingContext context, Throwable throwable) {
        context.response()
                .setStatusCode(SC_INTERNAL_SERVER_ERROR)
                .putHeader("Content-Type", "application/json")
                .end(JsonObject.mapFrom(ApiResponse.error(99, throwable.getLocalizedMessage())).encode());
    }
}
