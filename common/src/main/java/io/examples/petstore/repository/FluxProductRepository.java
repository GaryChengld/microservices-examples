package io.examples.petstore.repository;

import io.examples.petstore.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactor style product repository
 *
 * @author Gary Cheng
 */
public interface FluxProductRepository {

    Flux<Product> getProducts();

    Flux<Product> getProductsByCategory(String category);

    Mono<Product> getProductById(Integer id);

    Mono<Product> addProduct(Product product);

    Mono<Boolean> updateProduct(Product product);

    Mono<Boolean> deleteProduct(Integer id);
}
