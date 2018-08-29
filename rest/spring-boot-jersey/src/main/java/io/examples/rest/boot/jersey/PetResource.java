package io.examples.rest.boot.jersey;

import io.examples.common.domain.ApiResponse;
import io.examples.common.domain.Product;
import io.examples.common.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Gary Cheng
 */
@Slf4j
@Component
@Path("/v1/pet")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {

    @Autowired
    private ProductRepository productRepository;

    @GET
    public List<Product> all() {
        return productRepository.getProducts();
    }

    @GET
    @Path("{id}")
    public Response byId(@PathParam("id") Integer id) {
        Optional<Product> product = productRepository.getProductById(id);
        return product.map(p -> Response.ok().entity(product.get()).build())
                .orElseGet(this::petNotFound);
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
    public Response update(@PathParam("id") Integer id, Product product) {
        return productRepository.getProductById(id).map(p -> {
            product.setId(p.getId());
            productRepository.updateProduct(product);
            return Response.ok().entity(ApiResponse.message(1, "Update pet successfully")).build();
        }).orElseGet(this::petNotFound);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        return productRepository.getProductById(id).map(p -> {
            productRepository.deleteProduct(id);
            return Response.ok().entity(ApiResponse.message(2, "Delete pet successfully")).build();
        }).orElseGet(this::petNotFound);
    }

    private Response petNotFound() {
        return Response.status(Response.Status.NOT_FOUND).entity(ApiResponse.error(101, "Pet not found")).build();
    }
}
