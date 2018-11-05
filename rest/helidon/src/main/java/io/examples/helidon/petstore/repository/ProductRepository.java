package io.examples.helidon.petstore.repository;


import io.examples.helidon.petstore.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Product Repository
 *
 * @author Gary Cheng
 */
public interface ProductRepository {

    /**
     * Return all products
     *
     * @return
     */
    List<Product> getProducts();

    /**
     * Return products by category
     *
     * @param category
     * @return
     */
    List<Product> getProductsByCategory(String category);

    /**
     * Find product by id
     *
     * @param id
     * @return
     */
    Optional<Product> getProductById(Integer id);


    /**
     * Add a product
     *
     * @param product
     * @return
     */
    Product addProduct(Product product);

    /**
     * Update a product
     *
     * @param product
     */
    void updateProduct(Product product);

    /**
     * Delete product by ID
     *
     * @param id
     */
    void deleteProduct(Integer id);

}
