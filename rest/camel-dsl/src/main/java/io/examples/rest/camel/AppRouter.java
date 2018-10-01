package io.examples.rest.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

/**
 * Camel router configuration
 *
 * @author Gary Cheng
 */
@Component
public class AppRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration().component("restlet").host("localhost").port("9080").bindingMode(RestBindingMode.json);
        rest("/v1/pet")
                .consumes("application/json").produces("application/json")
                .get().to("bean:petService?method=all")
                .get("/findByCategory/{category}").to("bean:petService?method=byCategory(${header.category})")
                .get("/{id}").to("bean:petService?method=byId(${header.id})")
                .post().param().name("product").type(RestParamType.body).endParam().to("bean:petService?method=add)");

    }
}
