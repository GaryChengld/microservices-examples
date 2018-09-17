package io.examples.store.repository;

import io.examples.store.domain.Product;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static java.lang.Boolean.TRUE;

/**
 * Reactor style product repository
 *
 * @author Gary Cheng
 */
public class FluxProductRepository {
    private static FluxProductRepository instance = new FluxProductRepository();
    private ProductRepository repository = ProductRepository.instance();

    public static FluxProductRepository getInstance() {
        return instance;
    }

    public Flux<Product> getProducts() {
        return Flux.create(emitter -> {
            repository.getProducts().forEach(emitter::next);
            emitter.complete();
        });
    }

    public Flux<Product> getProductsByCategory(String category) {
        return Flux.create(emitter -> {
            repository.getProductsByCategory(category).forEach(emitter::next);
            emitter.complete();
        });
    }

    public Mono<Product> getProductById(Integer id) {
        return Mono.create(emitter -> {
            Optional<Product> product = repository.getProductById(id);
            if (product.isPresent()) {
                emitter.success(product.get());
            } else {
                emitter.success();
            }
        });
    }

    public Mono<Product> addProduct(Product product) {
        return Mono.create(emitter -> emitter.success(repository.addProduct(product)));
    }

    public Mono<Boolean> updateProduct(Product product) {
        return Mono.create(emitter -> {
            repository.updateProduct(product);
            emitter.success(TRUE);
        });
    }

    public Mono<Boolean> deleteProduct(Integer id) {
        return Mono.create(emitter -> {
            repository.deleteProduct(id);
            emitter.success(TRUE);
        });
    }
}
