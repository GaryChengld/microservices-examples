package io.examples.common.repository.adapters;

import io.examples.common.domain.Product;
import io.examples.common.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Mono;

/**
 * @author Gary Cheng
 */
public class ReactorProductRepositoryAdapter {
    private ProductRepository blockingRepository;
    private static ReactorProductRepositoryAdapter instance = new ReactorProductRepositoryAdapter();

    public static ReactorProductRepositoryAdapter getInstance() {
        return instance;
    }

    private ReactorProductRepositoryAdapter() {
        this.blockingRepository = ProductRepository.instance();
    }

    public Mono<List<Product>> getProducts() {
        return Mono.create(emitter -> emitter.success(blockingRepository.getProducts()));
    }

    public Mono<List<Product>> getProductsByCategory(String category) {
        return Mono.create(emitter -> emitter.success(blockingRepository.getProductsByCategory(category)));
    }

    public Mono<Product> getProductById(Integer id) {
        return Mono.create(emitter -> {
            Optional<Product> product = blockingRepository.getProductById(id);
            if (product.isPresent()) {
                emitter.success(product.get());
            } else {
                emitter.success();
            }
        });
    }

    public Mono<Product> addProduct(Product product) {
        return Mono.create(emitter -> blockingRepository.addProduct(product));
    }

    public Mono<Void> updateProduct(Product product) {
        return Mono.create(emitter -> blockingRepository.updateProduct(product));
    }

    public Mono<Void> deleteProduct(Integer id) {
        return Mono.create(emitter -> blockingRepository.deleteProduct(id));
    }
}
