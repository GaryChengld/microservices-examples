package io.examples.boot.handler;

import io.examples.common.ApiResponses;
import io.examples.store.domain.Product;
import io.examples.store.repository.FluxProductRepository;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import static io.examples.common.ApiResponses.MSG_DELETE_SUCCESS;
import static io.examples.common.ApiResponses.MSG_UPDATE_SUCCESS;
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
 * Functional handler for pet restful service
 *
 * @author Gary Cheng
 */
@Slf4j
public class PetHandler extends AbstractHandler {
    private final FluxProductRepository productRepository;

    public PetHandler(FluxProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Return routing configuration of pet handler
     *
     * @return RouterFunction
     */
    public RouterFunction<ServerResponse> getRouterFunction() {
        return nest(accept(APPLICATION_JSON),
                route(GET("/"), this::all)
                        .andRoute(GET("/{id}"), this::byId)
                        .andRoute(GET("/findByCategory/{category}"), this::byCategory)
                        .andRoute(POST("/"), this::add)
                        .andRoute(PUT("/{id}"), this::update)
                        .andRoute(DELETE("/{id}"), this::delete)
        );
    }

    private Mono<ServerResponse> all(ServerRequest request) {
        return ServerResponse.ok().
                contentType(APPLICATION_JSON)
                .body(productRepository.getProducts(), Product.class);
    }

    private Mono<ServerResponse> byId(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return productRepository.getProductById(id)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    private Mono<ServerResponse> byCategory(ServerRequest request) {
        String category = request.pathVariable("category");
        return ServerResponse.ok().
                contentType(APPLICATION_JSON)
                .body(productRepository.getProductsByCategory(category), Product.class);
    }

    private Mono<ServerResponse> add(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productRepository::addProduct)
                .flatMap(this::buildResponse);
    }

    private Mono<ServerResponse> update(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        AtomicReference<Product> productRef = new AtomicReference<>();
        return request.bodyToMono(Product.class)
                .flatMap(p -> {
                    p.setId(id);
                    productRef.set(p);
                    return productRepository.getProductById(id);
                })
                .flatMap(product -> productRepository.updateProduct(productRef.get()))
                .map(updated -> MSG_UPDATE_SUCCESS)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    private Mono<ServerResponse> delete(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(id))
                .map(deleted -> MSG_DELETE_SUCCESS)
                .flatMap(this::buildResponse)
                .switchIfEmpty(this.petNotFoundResponse());
    }

    private Mono<ServerResponse> petNotFoundResponse() {
        return ServerResponse.status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .body(fromObject(ApiResponses.ERR_PET_NOT_FOUND));
    }
}
