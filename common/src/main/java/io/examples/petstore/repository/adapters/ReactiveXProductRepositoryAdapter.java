package io.examples.petstore.repository.adapters;

import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.ProductRepository;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.List;
import java.util.Optional;

/**
 * Reactive Product repository on ReactiveX Java 2.0
 *
 * @author Gary Cheng
 */
public class ReactiveXProductRepositoryAdapter {
    private ProductRepository blockingRepository;
    private static ReactiveXProductRepositoryAdapter instance = new ReactiveXProductRepositoryAdapter();

    public static ReactiveXProductRepositoryAdapter getInstance() {
        return instance;
    }

    private ReactiveXProductRepositoryAdapter() {
        this.blockingRepository = ProductRepository.instance();
    }

    public Single<List<Product>> getProducts() {
        return Single.create(emitter -> emitter.onSuccess(blockingRepository.getProducts()));
    }

    public Single<List<Product>> getProductsByCategory(String category) {
        return Single.create(emitter -> emitter.onSuccess(blockingRepository.getProductsByCategory(category)));
    }

    public Maybe<Product> getProductById(Integer id) {
        return Maybe.create(emitter -> {
            Optional<Product> product = blockingRepository.getProductById(id);
            if (product.isPresent()) {
                emitter.onSuccess(product.get());
            } else {
                emitter.onComplete();
            }
        });
    }

    public Single<Product> addProduct(Product product) {
        return Single.create(emitter -> emitter.onSuccess(blockingRepository.addProduct(product)));
    }

    public Completable updateProduct(Product product) {
        return Completable.create(emitter -> {
            blockingRepository.updateProduct(product);
            emitter.onComplete();
        });
    }

    public Completable deleteProduct(Integer id) {
        return Completable.create(emitter -> {
            blockingRepository.deleteProduct(id);
            emitter.onComplete();
        });
    }
}
