package io.examples.apigateway.boot.Service;

import io.examples.boot.handler.PetHandler;
import io.examples.store.repository.FluxProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;


/**
 * Main Application
 *
 * @author Gary Cheng
 */
@SpringBootApplication
@Configuration
public class PetService {

    @Bean
    public PetHandler petHandler() {
        return new PetHandler(FluxProductRepository.getInstance());
    }

    @Bean
    public RouterFunction<ServerResponse> petRouterFunction(PetHandler petHandler) {
        return nest(path("/v1/pet"), petHandler.getRouterFunction());
    }

    public static void main(String[] args) {
        SpringApplication.run(PetService.class, args);
    }
}
