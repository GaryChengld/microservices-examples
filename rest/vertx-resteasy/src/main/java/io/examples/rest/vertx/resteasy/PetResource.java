package io.examples.rest.vertx.resteasy;

import io.examples.common.ApiResponse;
import io.examples.common.ApiResponses;
import io.examples.store.domain.Product;
import io.examples.store.repository.RxProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.examples.common.ApiResponses.MSG_DELETE_SUCCESS;
import static io.examples.common.ApiResponses.MSG_UPDATE_SUCCESS;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * @author Gary Cheng
 */
@Path("/v1/pet")
@Produces(MediaType.APPLICATION_JSON)
public class PetResource {
    private static final Logger logger = LoggerFactory.getLogger(PetResource.class);
    private RxProductRepository productRepository;

    private PetResource(RxProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static PetResource create(RxProductRepository productRepository) {
        PetResource petResource = new PetResource(productRepository);
        return petResource;
    }

    @GET
    public void all(@Suspended final AsyncResponse asyncResponse) {
        logger.debug("Received get all pets request");
        productRepository.getProducts()
                .toList()
                .subscribe(products -> asyncResponse.resume(this.buildResponse(products)));
    }

    @GET
    @Path("{id}")
    public void byId(@Suspended final AsyncResponse asyncResponse, @PathParam("id") Integer id) {
        logger.debug("Received byId request, id={}", id);
        productRepository.getProductById(id)
                .subscribe(p -> asyncResponse.resume(this.buildResponse(p)),
                        t -> asyncResponse.resume(this.exceptionResponse(t)),
                        () -> asyncResponse.resume(this.petNotFoundResponse()));

    }

    @GET
    @Path("/findByCategory/{category}")
    public void byCategory(@Suspended final AsyncResponse asyncResponse, @PathParam("category") String category) {
        logger.debug("received findByCategory request");
        productRepository.getProductsByCategory(category)
                .toList()
                .subscribe(products -> asyncResponse.resume(this.buildResponse(products)));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(@Suspended final AsyncResponse asyncResponse, Product product) {
        productRepository.addProduct(product)
                .subscribe(p -> asyncResponse.resume(this.buildResponse(p)));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void update(@Suspended final AsyncResponse asyncResponse, @PathParam("id") Integer id, Product product) {
        product.setId(id);
        productRepository.getProductById(id)
                .flatMap(p -> productRepository.updateProduct(product).toMaybe())
                .subscribe(p -> asyncResponse.resume(this.buildResponse(MSG_UPDATE_SUCCESS)),
                        t -> asyncResponse.resume(this.exceptionResponse(t)),
                        () -> asyncResponse.resume(this.petNotFoundResponse()));
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public void delete(@Suspended final AsyncResponse asyncResponse, @PathParam("id") Integer id) {
        productRepository.getProductById(id)
                .flatMap(p -> productRepository.deleteProduct(id).toMaybe())
                .subscribe(p -> asyncResponse.resume(this.buildResponse(MSG_DELETE_SUCCESS)),
                        t -> asyncResponse.resume(this.exceptionResponse(t)),
                        () -> asyncResponse.resume(this.petNotFoundResponse()));
    }

    private <T> Response buildResponse(T body) {
        return Response.status(OK).entity(body).build();
    }

    private Response petNotFoundResponse() {
        return Response.status(NOT_FOUND).entity(ApiResponses.ERR_PET_NOT_FOUND).build();
    }

    private Response exceptionResponse(Throwable throwable) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.error(99, throwable.getLocalizedMessage()))
                .build();
    }
}
