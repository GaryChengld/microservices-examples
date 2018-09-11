package io.examples.rest.boot;

import io.examples.common.ApiResponse;
import io.examples.store.ApiResponses;
import io.examples.store.domain.Product;
import io.examples.store.repository.FluxProductRepository;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Pet Controller on spring-webflux
 *
 * @author Gary Cheng
 */
@RestController
@RequestMapping(value = "/v1/pet", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PetController {
    private static final ResponseEntity<ApiResponse> RESP_PET_NOT_FOUND
            = new ResponseEntity<>(ApiResponses.ERR_PET_NOT_FOUND, HttpStatus.NOT_FOUND);

    @Autowired
    private FluxProductRepository productRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Flux<Product> all() {
        return productRepository.getProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Mono<ResponseEntity<?>> byId(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .defaultIfEmpty(RESP_PET_NOT_FOUND);
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    @ResponseBody
    public Flux<Product> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Product> add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<ApiResponse> update(@PathVariable("id") Integer id, @RequestBody Product product) {
        return productRepository.getProductById(id)
                .flatMap(p -> {
                    product.setId(p.getId());
                    return productRepository.updateProduct(product);
                })
                .map(updated -> ApiResponses.MSG_UPDATE_SUCCESS)
                .defaultIfEmpty(ApiResponses.ERR_PET_NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Mono<ApiResponse> delete(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(id))
                .map(deleted -> ApiResponses.MSG_DELETE_SUCCESS)
                .defaultIfEmpty(ApiResponses.ERR_PET_NOT_FOUND);
    }
}
