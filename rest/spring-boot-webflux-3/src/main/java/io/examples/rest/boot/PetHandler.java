package io.examples.rest.boot;

import io.examples.petstore.ApiResponses;
import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.adapters.ReactorProductRepositoryAdapter;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static io.examples.petstore.ApiResponses.MSG_DELETE_SUCCESS;
import static io.examples.petstore.ApiResponses.MSG_UPDATE_SUCCESS;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * Functional handler for pet restful service
 *
 * @author Gary Cheng
 */
@Slf4j
public class PetHandler {
    private final ReactorProductRepositoryAdapter productRepository;

    public PetHandler(ReactorProductRepositoryAdapter productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all pets
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> all(ServerRequest request) {
        return productRepository.getProducts().flatMap(this::buildResponse);
    }

    /**
     * Find pet by Id
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> byId(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return productRepository.getProductById(id)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    /**
     * Find pets by category
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> byCategory(ServerRequest request) {
        String category = request.pathVariable("category");
        return productRepository.getProductsByCategory(category).flatMap(this::buildResponse);
    }

    /**
     * Add a new Pet
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> add(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productRepository::addProduct)
                .flatMap(this::buildResponse);
    }

    /**
     * Update a pet
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> update(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        AtomicReference<Product> productRef = new AtomicReference<>();
        return request.bodyToMono(Product.class)
                .flatMap(p -> {
                    p.setId(id);
                    productRef.set(p);
                    return productRepository.getProductById(id);
                })
                .flatMap(product -> productRepository.updateProduct(productRef.get()))
                .map(product -> MSG_UPDATE_SUCCESS)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    /**
     * Delete a pet
     *
     * @param request server request
     * @return
     */
    public Mono<ServerResponse> delete(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(id))
                .map(deleted -> MSG_DELETE_SUCCESS)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    private <T> Mono<ServerResponse> buildResponse(T body) {
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(body));
    }

    private Mono<ServerResponse> petNotFoundResponse() {
        return ServerResponse.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(fromObject(ApiResponses.ERR_PET_NOT_FOUND));
    }
}
