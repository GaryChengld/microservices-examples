package io.examples.petstore.repository;

import io.examples.petstore.domain.Product;
import io.reactivex.Completable;
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
