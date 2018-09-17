package io.examples.apigateway.boot.Service;

import io.examples.boot.handler.ReviewHandler;
import io.examples.review.repository.FluxReviewRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

/**
 * Main Application of Review service
 *
 * @author Gary Cheng
 */
@SpringBootApplication
@Configuration
public class ReviewService {

    @Bean
    public ReviewHandler reviewHandler() {
        return new ReviewHandler(FluxReviewRepository.getInstance());
    }

    @Bean
    public RouterFunction<ServerResponse> petRouterFunction(ReviewHandler reviewHandler) {
        return nest(path("/v1/review"), reviewHandler.getRouterFunction());
    }

    public static void main(String[] args) {
        SpringApplication.run(ReviewService.class, args);
    }
}
