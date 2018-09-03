package io.examples.petstore.repository.impl;

import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.ProductRepository;
import io.examples.petstore.repository.RxProductRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Optional;


import static io.reactivex.BackpressureStrategy.DROP;

/**
 * @author Gary Cheng
 */
public class RxProductRepositoryImpl implements RxProductRepository {

    private ProductRepository repository = ProductRepository.instance();

    @Override
    public Flowable<Product> getProducts() {
        return Flowable.create(emitter -> {
            repository.getProducts().forEach(emitter::onNext);
            emitter.onComplete();
        }, DROP);
    }

    @Override
    public Flowable<Product> getProductsByCategory(String category) {
        return Flowable.create(emitter -> {
            repository.getProductsByCategory(category).forEach(emitter::onNext);
            emitter.onComplete();
        }, DROP);
    }

    @Override
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

    @Override
    public Single<Product> addProduct(Product product) {
        return Single.create(emitter -> emitter.onSuccess(repository.addProduct(product)));
    }

    @Override
    public Single<Boolean> updateProduct(Product product) {
        return Single.create(emitter -> {
            repository.updateProduct(product);
            emitter.onSuccess(Boolean.TRUE);
        });
    }

    @Override
    public Single<Boolean> deleteProduct(Integer id) {
        return Single.create(emitter -> {
            repository.deleteProduct(id);
            emitter.onSuccess(Boolean.TRUE);
        });
    }
}
