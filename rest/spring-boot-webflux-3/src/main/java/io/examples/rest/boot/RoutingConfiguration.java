package io.examples.rest.boot;

import io.examples.store.repository.FluxProductRepository;
import io.examples.store.repository.impl.FluxProductRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * The routing configuration of PetStore restful service
 *
 * @author Gary Cheng
 */
@Configuration
public class RoutingConfiguration {

    @Bean
    public FluxProductRepository productRepository() {
        return new FluxProductRepositoryImpl();
    }

    @Bean
    public PetHandler petHandler(FluxProductRepository productRepository) {
        return new PetHandler(productRepository);
    }

    @Bean
    public RouterFunction<ServerResponse> petRouterFunction(PetHandler petHandler) {
        return nest(path("/v1/pet"),
                nest(accept(APPLICATION_JSON),
                        route(GET("/"), petHandler::all)
                                .andRoute(GET("/{id}"), petHandler::byId)
                                .andRoute(GET("/findByCategory/{category}"), petHandler::byCategory)
                                .andRoute(POST("/"), petHandler::add)
                                .andRoute(PUT("/{id}"), petHandler::update)
                                .andRoute(DELETE("/{id}"), petHandler::delete)
                )
        );
    }
}
