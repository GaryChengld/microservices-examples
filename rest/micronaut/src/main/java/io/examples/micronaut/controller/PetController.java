package io.examples.micronaut.controller;

import io.examples.micronaut.common.ApiResponse;
import io.examples.micronaut.common.ApiResponses;
import io.examples.micronaut.entity.Product;
import io.examples.micronaut.health.Health;
import io.examples.micronaut.repository.ProductRepository;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.reactivex.Single;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * @author Gary Cheng
 */
@Controller("/v1/pet")
public class PetController {
    @Inject
    private ProductRepository productRepository;

    @Get("/version")
    public Health version() {
        return new Health("1.0", "OK");
    }

    @Get
    public Single<List<Product>> all() {
        return Single.create(emitter -> emitter.onSuccess(productRepository.getProducts()));
    }

    @Get("/{id}")
    public Single<?> byId(Integer id) {
        return Single.create(emitter -> {
            Optional<Product> product = productRepository.getProductById(id);
            if (product.isPresent()) {
                emitter.onSuccess(product.get());
            } else {
                emitter.onSuccess(ApiResponses.ERR_PET_NOT_FOUND);
            }
        });
    }

    @Get("/findByCategory/{category}")
    public Single<List<Product>> byCategory(String category) {
        return Single.create(emitter -> emitter.onSuccess(productRepository.getProductsByCategory(category)));
    }

    @Post()
    public Single<Product> add(@Body Product product) {
        return Single.create(emitter -> {
            Product newProduct = productRepository.addProduct(product);
            emitter.onSuccess(newProduct);
        });
    }

    @Put("/{id}")
    public Single<ApiResponse> update(Integer id, @Body Product product) {
        return Single.create(emitter -> {
            ApiResponse response = productRepository.getProductById(id)
                    .map(p -> {
                        product.setId(p.getId());
                        productRepository.updateProduct(product);
                        return ApiResponses.MSG_UPDATE_SUCCESS;
                    }).orElse(ApiResponses.ERR_PET_NOT_FOUND);
            emitter.onSuccess(response);
        });
    }

    @Delete("/{id}")
    public Single<ApiResponse> delete(Integer id) {
        return Single.create(emitter -> {
            ApiResponse response = productRepository.getProductById(id)
                    .map(p -> {
                        productRepository.deleteProduct(id);
                        return ApiResponses.MSG_DELETE_SUCCESS;
                    })
                    .orElse(ApiResponses.ERR_PET_NOT_FOUND);
            emitter.onSuccess(response);
        });
    }
}