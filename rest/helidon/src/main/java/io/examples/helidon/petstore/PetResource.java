package io.examples.helidon.petstore;

import io.examples.helidon.petstore.common.ApiResponse;
import io.examples.helidon.petstore.common.ApiResponses;
import io.examples.helidon.petstore.entity.Product;
import io.examples.helidon.petstore.repository.ProductRepository;
import io.examples.helidon.petstore.repository.impl.InMemoryProductRepository;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * @author Gary Cheng
 */
@Path("/v1/pet")
@RequestScoped
@Slf4j
public class PetResource {
    private static final Response RESP_PET_NOT_FOUND
            = Response.status(Response.Status.NOT_FOUND).entity(ApiResponses.ERR_PET_NOT_FOUND).build();

    private ProductRepository productRepository = InMemoryProductRepository.getInstance();

    @GET
    public List<Product> all() {
        return productRepository.getProducts();
    }

    @GET
    @Path("{id}")
    public Response byId(@PathParam("id") Integer id) {
        Optional<Product> product = productRepository.getProductById(id);
        return product.map(p -> Response.ok().entity(product.get()).build())
                .orElse(RESP_PET_NOT_FOUND);
    }

    @GET
    @Path("/findByCategory/{category}")
    public List<Product> byCategory(@PathParam("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Product add(Product product) {
        log.debug("Add new pet:{}", product);
        return productRepository.addProduct(product);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ApiResponse update(@PathParam("id") Integer id, Product product) {
        return productRepository.getProductById(id).map(p -> {
            product.setId(p.getId());
            productRepository.updateProduct(product);
            return ApiResponses.MSG_UPDATE_SUCCESS;
        }).orElse(ApiResponses.ERR_PET_NOT_FOUND);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ApiResponse delete(@PathParam("id") Integer id) {
        return productRepository.getProductById(id).map(p -> {
            productRepository.deleteProduct(id);
            return ApiResponses.MSG_DELETE_SUCCESS;
        }).orElse(ApiResponses.ERR_PET_NOT_FOUND);
    }
}
