package io.examples.store.repository.impl;

import io.examples.store.domain.Product;
import io.examples.store.repository.FluxProductRepository;
import io.examples.store.repository.ProductRepository;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static java.lang.Boolean.TRUE;

/**
 * @author Gary Cheng
 */
public class FluxProductRepositoryImpl implements FluxProductRepository {

    private ProductRepository repository = ProductRepository.instance();

    @Override
    public Flux<Product> getProducts() {
        return Flux.create(emitter -> {
            repository.getProducts().forEach(emitter::next);
            emitter.complete();
        });
    }

    @Override
    public Flux<Product> getProductsByCategory(String category) {
        return Flux.create(emitter -> {
            repository.getProductsByCategory(category).forEach(emitter::next);
            emitter.complete();
        });
    }

    @Override
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

    @Override
    public Mono<Product> addProduct(Product product) {
        return Mono.create(emitter -> emitter.success(repository.addProduct(product)));
    }

    @Override
    public Mono<Boolean> updateProduct(Product product) {
        return Mono.create(emitter -> {
            repository.updateProduct(product);
            emitter.success(TRUE);
        });
    }

    @Override
    public Mono<Boolean> deleteProduct(Integer id) {
        return Mono.create(emitter -> {
            repository.deleteProduct(id);
            emitter.success(TRUE);
        });
    }
}
