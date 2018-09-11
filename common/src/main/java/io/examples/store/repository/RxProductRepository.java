package io.examples.store.repository;

import io.examples.store.domain.Product;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * ReactiveX style product repository
 *
 * @author Gary Cheng
 */
public interface RxProductRepository {

    Flowable<Product> getProducts();

    Flowable<Product> getProductsByCategory(String category);

    Maybe<Product> getProductById(Integer id);

    Single<Product> addProduct(Product product);

    Single<Boolean> updateProduct(Product product);

    Single<Boolean> deleteProduct(Integer id);
}
