package io.examples.rest.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
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
        restConfiguration().component("netty").host("localhost").port("9080").bindingMode(RestBindingMode.json);
    }
}
