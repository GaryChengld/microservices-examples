package io.examples.rest.boot;

import io.examples.common.domain.ApiResponse;
import io.examples.common.domain.Product;
import io.examples.common.repository.adapters.ReactorProductRepositoryAdapter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Gary Cheng
 */
@RestController
@RequestMapping(value = "/v1/pet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PetController {

    @Autowired
    private ReactorProductRepositoryAdapter productRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Mono<List<Product>> all() {
        return productRepository.getProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Mono<ResponseEntity<?>> byId(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .<ResponseEntity<?>>map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(this.petNotFound());
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    @ResponseBody
    public Mono<List<Product>> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Product> add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> update(@PathVariable("id") Integer id, @RequestBody Product product) {
        return productRepository.getProductById(id)
                .flatMap(p -> {
                    product.setId(p.getId());
                    return productRepository.updateProduct(product);
                })
                .map(p -> this.apiResponse(1, "Update pet successfully"))
                .defaultIfEmpty(this.petNotFound());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Mono<ResponseEntity> delete(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(id))
                .map(i -> this.apiResponse(2, "Delete pet successfully"))
                .defaultIfEmpty(this.petNotFound());
    }

    private ResponseEntity<ApiResponse> petNotFound() {
        return new ResponseEntity<>(ApiResponse.error(101, "Pet not found"), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity apiResponse(int code, String message) {
        return ResponseEntity.ok(ApiResponse.message(code, message));
    }
}
