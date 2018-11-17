package io.examples.cqrs.petstore.controller;

import io.examples.cqrs.petstore.command.CreateProductCommand;
import io.examples.cqrs.petstore.common.ApiResponse;
import io.examples.cqrs.petstore.common.ApiResponses;
import io.examples.cqrs.petstore.queryobject.ProductQueryObject;
import io.examples.cqrs.petstore.queryobject.ProductQueryObjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Gary Cheng
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/pet", produces = MediaType.APPLICATION_JSON_VALUE)
public class PetController {
    private static final ResponseEntity<ApiResponse> RESP_PET_NOT_FOUND
            = new ResponseEntity<>(ApiResponses.ERR_PET_NOT_FOUND, HttpStatus.NOT_FOUND);

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private ProductQueryObjectRepository productQueryObjectRepository;

    @GetMapping
    public List<ProductQueryObject> findAll() {
        return productQueryObjectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable String id) {
        return productQueryObjectRepository
                .findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(RESP_PET_NOT_FOUND);
    }

    @PostMapping
    public CompletableFuture<String> createProduct(@RequestBody Map<String, String> request) {
        String id = UUID.randomUUID().toString();
        return commandGateway.send(new CreateProductCommand(id, request.get("name"), request.get("category")));
    }
}
