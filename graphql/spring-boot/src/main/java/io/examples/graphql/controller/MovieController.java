package io.examples.graphql.controller;

import graphql.ExecutionResult;
import io.examples.graphql.service.GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Gary Cheng
 */
@RestController
@RequestMapping(value = "/v1/movies")
public class MovieController {
    @Autowired
    private GraphQLService graphQLService;

    @PostMapping
    public Mono<ResponseEntity<Object>> query(@RequestBody String query) {
        return Mono.create(emitter -> {
            ExecutionResult executeResult = graphQLService.getGraphQL().execute(query);
            emitter.success(new ResponseEntity<>(executeResult, HttpStatus.OK));
        });
    }
}
