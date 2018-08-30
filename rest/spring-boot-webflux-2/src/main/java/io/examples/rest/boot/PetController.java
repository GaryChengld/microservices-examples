package io.examples.rest.boot;

import io.examples.common.ApiResponse;
import io.examples.petstore.ApiResponses;
import io.examples.petstore.domain.Product;
import io.examples.petstore.repository.ProductRepository;
import java.util.Collection;
import java.util.function.Supplier;
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
    private ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Flux<Product> all() {
        return this.toFlux(productRepository::getProducts);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Mono<ResponseEntity<?>> byId(@PathVariable("id") Integer id) {
        return this.toMono(() -> productRepository.getProductById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(RESP_PET_NOT_FOUND)
        );
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    @ResponseBody
    public Flux<Product> byCategory(@PathVariable("category") String category) {
        return this.toFlux(() -> productRepository.getProductsByCategory(category));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Product> add(@RequestBody Product product) {
        return this.toMono(() -> productRepository.addProduct(product));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<ApiResponse> update(@PathVariable("id") Integer id, @RequestBody Product product) {
        return this.toMono(() -> productRepository.getProductById(id)
                .map(p -> {
                    product.setId(p.getId());
                    productRepository.updateProduct(product);
                    return ApiResponses.MSG_UPDATE_SUCCESS;
                })
                .orElse(ApiResponses.ERR_PET_NOT_FOUND)
        );
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Mono<ApiResponse> delete(@PathVariable("id") Integer id) {
        return this.toMono(() -> productRepository.getProductById(id)
                .map(p -> {
                    productRepository.deleteProduct(id);
                    return ApiResponses.MSG_DELETE_SUCCESS;
                })
                .orElse(ApiResponses.ERR_PET_NOT_FOUND)
        );
    }

    private <T> Flux<T> toFlux(Supplier<Collection<T>> supplier) {
        return Flux.create(emitter -> {
            supplier.get().forEach(emitter::next);
            emitter.complete();
        });
    }

    private <T> Mono<T> toMono(Supplier<T> supplier) {
        return Mono.create(emitter -> emitter.success(supplier.get()));
    }
}
