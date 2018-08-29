package io.examples.rest.boot;

import io.examples.common.domain.ApiResponse;
import io.examples.common.domain.Product;
import io.examples.common.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @ResponseBody
    public List<Product> all() {
        return productRepository.getProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> byId(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(this::petNotFound);
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    @ResponseBody
    public List<Product> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Product add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> update(@PathVariable("id") Integer id, @RequestBody Product product) {
        return productRepository.getProductById(id).map(p -> {
            product.setId(p.getId());
            productRepository.updateProduct(product);
            return ResponseEntity.ok(ApiResponse.message(1, "Update pet successfully"));
        }).orElseGet(this::petNotFound);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResponse> delete(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id).map(p -> {
            productRepository.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.message(2, "Delete pet successfully"));
        }).orElseGet(this::petNotFound);
    }

    private ResponseEntity<ApiResponse> petNotFound() {
        return new ResponseEntity<>(ApiResponse.error(101, "Pet not found"), HttpStatus.NOT_FOUND);
    }
}
