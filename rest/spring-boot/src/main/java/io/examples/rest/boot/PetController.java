package io.examples.rest.boot;

import io.examples.common.domain.ApiResponse;
import io.examples.common.domain.Product;
import io.examples.common.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gary Cheng
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/pet", produces = MediaType.APPLICATION_JSON_VALUE)
public class PetController {

    @Autowired
    private ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Product> all() {
        return productRepository.getProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> byId(@PathVariable("id") Integer id) {
        Optional<Product> product = productRepository.getProductById(id);
        return product.<ResponseEntity<?>>map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(this::petNotFound);
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    public List<Product> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Product add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> update(@PathVariable("id") Integer id, @RequestBody Product product) {
        Optional<Product> optional = productRepository.getProductById(id);
        if (optional.isPresent()) {
            product.setId(optional.get().getId());
            productRepository.updateProduct(product);
            return new ResponseEntity<>(ApiResponse.message(1, "Update pet successfully"), HttpStatus.OK);
        } else {
            return this.petNotFound();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Integer id) {
        Optional<Product> optional = productRepository.getProductById(id);
        if (optional.isPresent()) {
            productRepository.deleteProduct(id);
            return new ResponseEntity<>(ApiResponse.message(2, "Delete pet successfully"), HttpStatus.OK);
        } else {
            return this.petNotFound();
        }
    }

    private ResponseEntity<ApiResponse> petNotFound() {
        return new ResponseEntity<>(ApiResponse.error(101, "Pet not found"), HttpStatus.NOT_FOUND);
    }
}
