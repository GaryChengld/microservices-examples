package io.examples.rest.vertx.resteasy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gary Cheng
 */
@Path("/api/greetings")
public class GreetingResource {
    private static final Logger logger = LoggerFactory.getLogger(GreetingResource.class);

    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public void greeting(@Suspended final AsyncResponse asyncResponse) {
        logger.debug("Received greeting request");
        Greeting greeting = new Greeting("Hello World");
        asyncResponse.resume(Response.status(Response.Status.OK).entity(greeting).build());
        logger.debug("Handling greeting request completed");
    }

    @GET
    @Path("/{name}")
    @Produces({MediaType.APPLICATION_JSON})
    public void greetingWithName(@Suspended final AsyncResponse asyncResponse, @PathParam("name") String name) {
        logger.debug("Received greetingWithName request");
        Greeting greeting = new Greeting("Hello " + name);
        asyncResponse.resume(Response.status(Response.Status.OK).entity(greeting).build());
        logger.debug("Handling greetingWithName request completed");
    }
}
