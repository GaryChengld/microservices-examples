package io.examples.rest.camel;

import io.examples.common.ApiResponses;
import io.examples.store.domain.Product;
import io.examples.store.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

/**
 * @author Gary Cheng
 */

@Component
@Slf4j
public class PetService {
    @Autowired
    ProductRepository productRepository;

    /**
     * Return all pets
     *
     * @return
     */
    public List<Product> all() {
        return productRepository.getProducts();
    }

    /**
     * Find pet by id
     *
     * @param id
     * @return
     */
    public Object byId(String id) {
        log.debug("Received byId request id:{}", id);
        Optional optional = productRepository.getProductById(Integer.valueOf(id));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return ApiResponses.ERR_PET_NOT_FOUND;
        }
    }

    /**
     * Find pet by category
     *
     * @param category
     * @return
     */
    public List<Product> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    /**
     * Add a pet
     *
     * @param product
     * @return
     */
    public Product add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }
}
