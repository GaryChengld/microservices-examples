package io.examples.light4j.petstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.networknt.handler.util.Exchange;
import io.examples.common.ApiResponse;
import io.examples.petstore.ApiResponses;
import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.RxProductRepository;
import io.reactivex.Single;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The restful handler of PetStore service
 *
 * @author Gary Cheng
 */
public class PetHandler {
    private static final Logger logger = LoggerFactory.getLogger(PetHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RxProductRepository productRepository;

    private PetHandler(RxProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Create a PetHandler instance
     *
     * @param productRepository
     * @return
     */
    public static PetHandler create(RxProductRepository productRepository) {
        return new PetHandler(productRepository);
    }

    /**
     * Get all pets
     *
     * @param exchange
     */
    public void all(HttpServerExchange exchange) {
        logger.debug("Received all pets request");
        productRepository.getProducts()
                .toList()
                .subscribe(products -> this.buildResponse(exchange, products));
    }

    /**
     * Find pet by ID
     *
     * @param exchange
     */
    public void byId(HttpServerExchange exchange) {
        int id = Exchange.pathParams().pathParamAsLong(exchange, "id").orElse(0L).intValue();
        logger.debug("Received find pet by ID request, id:{}", id);
        productRepository.getProductById(id)
                .subscribe(product -> this.buildResponse(exchange, product),
                        t -> this.exceptionResponse(exchange, t),
                        () -> this.notFoundResponse(exchange));
    }

    /**
     * Find pets by category
     *
     * @param exchange
     */
    public void byCategory(HttpServerExchange exchange) {
        String category = Exchange.pathParams().pathParam(exchange, "category").get();
        logger.debug("Received find pet by Category request, category:{}", category);
        productRepository.getProductsByCategory(category)
                .toList()
                .subscribe(products -> this.buildResponse(exchange, products));
    }

    /**
     * Add a new pet
     *
     * @param exchange
     */
    public void add(HttpServerExchange exchange) {
        logger.debug("Received add pet request");
        this.rxReceiveBody(exchange)
                .map(body -> this.jsonToObject(body, Product.class))
                .flatMap(product -> productRepository.addProduct(product))
                .subscribe(product -> this.buildResponse(exchange, product), t -> this.exceptionResponse(exchange, t));
    }

    public void update(HttpServerExchange exchange) {
        int id = Exchange.pathParams().pathParamAsLong(exchange, "id").orElse(0L).intValue();
        logger.debug("Received update pet request, pet id:{}", id);
        productRepository.getProductById(id)
                .flatMap(p -> this.rxReceiveBody(exchange).toMaybe())
                .map(body -> this.jsonToObject(body, Product.class))
                .doOnSuccess(product -> product.setId(id))
                .flatMap(product -> productRepository.updateProduct(product).toMaybe())
                .subscribe(b -> this.buildResponse(exchange, ApiResponses.MSG_UPDATE_SUCCESS),
                        t -> this.exceptionResponse(exchange, t),
                        () -> this.notFoundResponse(exchange));
    }

    public void delete(HttpServerExchange exchange) {
        int id = Exchange.pathParams().pathParamAsLong(exchange, "id").orElse(0L).intValue();
        logger.debug("Received delete pet request, pet id:{}", id);
        productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(p.getId()).toMaybe())
                .subscribe(b -> this.buildResponse(exchange, ApiResponses.MSG_DELETE_SUCCESS),
                        t -> this.exceptionResponse(exchange, t),
                        () -> this.notFoundResponse(exchange));
    }

    private <T> void buildResponse(HttpServerExchange exchange, T body) {
        this.buildResponse(exchange, HttpResponseCodes.SC_OK, body);
    }

    private void exceptionResponse(HttpServerExchange exchange, Throwable throwable) {
        this.buildResponse(exchange, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, ApiResponse.error(99, throwable.getLocalizedMessage()));
    }

    private void notFoundResponse(HttpServerExchange exchange) {
        this.buildResponse(exchange, HttpResponseCodes.SC_NOT_FOUND, ApiResponses.ERR_PET_NOT_FOUND);
    }

    private <T> void buildResponse(HttpServerExchange exchange, int statusCode, T body) {
        ObjectWriter writer = objectMapper.writer().withDefaultPrettyPrinter();
        exchange.getResponseHeaders().add(new HttpString("Content-Type"), "application/json");
        exchange.setStatusCode(statusCode);
        try {
            String jsonString = writer.writeValueAsString(body);
            exchange.getResponseSender().send(jsonString);
        } catch (JsonProcessingException e) {
            exchange.getResponseSender().send(e.getLocalizedMessage());
        }
    }

    private <T> T jsonToObject(String json, Class<T> type) {
        logger.debug("jsonToObject, json:{}", json);
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Single<String> rxReceiveBody(HttpServerExchange exchange) {
        return Single.create(emitter ->
                exchange.getRequestReceiver().receiveFullString((ex, body) -> emitter.onSuccess(body)));
    }
}
