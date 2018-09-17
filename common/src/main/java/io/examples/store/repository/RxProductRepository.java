package io.examples.store.repository;

import io.examples.store.domain.Product;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Optional;


import static io.reactivex.BackpressureStrategy.DROP;

/**
 * ReactiveX style product repository
 *
 * @author Gary Cheng
 */
public class RxProductRepository {
    private static RxProductRepository instance = new RxProductRepository();
    private ProductRepository repository = ProductRepository.instance();

    public static RxProductRepository getInstance() {
        return instance;
    }

    public Flowable<Product> getProducts() {
        return Flowable.create(emitter -> {
            repository.getProducts().forEach(emitter::onNext);
            emitter.onComplete();
        }, DROP);
    }

    public Flowable<Product> getProductsByCategory(String category) {
        return Flowable.create(emitter -> {
            repository.getProductsByCategory(category).forEach(emitter::onNext);
            emitter.onComplete();
        }, DROP);
    }

    public Maybe<Product> getProductById(Integer id) {
        return Maybe.create(emitter -> {
            Optional<Product> product = repository.getProductById(id);
            if (product.isPresent()) {
                emitter.onSuccess(product.get());
            } else {
                emitter.onComplete();
            }
        });
    }

    public Single<Product> addProduct(Product product) {
        return Single.create(emitter -> emitter.onSuccess(repository.addProduct(product)));
    }

    public Single<Boolean> updateProduct(Product product) {
        return Single.create(emitter -> {
            repository.updateProduct(product);
            emitter.onSuccess(Boolean.TRUE);
        });
    }

    public Single<Boolean> deleteProduct(Integer id) {
        return Single.create(emitter -> {
            repository.deleteProduct(id);
            emitter.onSuccess(Boolean.TRUE);
        });
    }
}
