package io.examples.petstore.repository;

import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.impl.InMemoryProductRepository;
import java.util.List;
import java.util.Optional;

/**
 * @author Gary Cheng
 */
public interface ProductRepository {

    static ProductRepository instance() {
        return InMemoryProductRepository.getInstance();
    }
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
