package io.examples.spring.hateoas.controller;


import io.examples.spring.hateoas.common.ApiResponse;
import io.examples.spring.hateoas.common.ApiResponses;
import io.examples.spring.hateoas.entity.Product;
import io.examples.spring.hateoas.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
    private ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.GET, produces = {"application/hal+json"})
    @ResponseBody
    public Resources<Product> all() {
        List<Product> products = productRepository.getProducts().stream()
                .map(this::addLinks)
                .collect(Collectors.toList());
        Link link = linkTo(this.getClass()).withSelfRel();
        return new Resources<>(products, link);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/hal+json"})
    public ResponseEntity<?> byId(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id)
                .map(this::addLinks)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(RESP_PET_NOT_FOUND);
    }

    @RequestMapping(value = "/findByCategory/{category}", method = RequestMethod.GET)
    @ResponseBody
    public List<Product> byCategory(@PathVariable("category") String category) {
        return productRepository.getProductsByCategory(category);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Product add(@RequestBody Product product) {
        return productRepository.addProduct(product);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApiResponse update(@PathVariable("id") Integer id, @RequestBody Product product) {
        return productRepository.getProductById(id).map(p -> {
            product.setProductId(p.getProductId());
            productRepository.updateProduct(product);
            return ApiResponses.MSG_UPDATE_SUCCESS;
        }).orElse(ApiResponses.ERR_PET_NOT_FOUND);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ApiResponse delete(@PathVariable("id") Integer id) {
        return productRepository.getProductById(id).map(p -> {
            productRepository.deleteProduct(id);
            return ApiResponses.MSG_DELETE_SUCCESS;
        }).orElse(ApiResponses.ERR_PET_NOT_FOUND);
    }

    private Product addLinks(Product product) {
        product.removeLinks();
        Link allLink = linkTo(methodOn(this.getClass()).all()).withRel("all-pets");
        product.add(allLink);
        Link selfLink = linkTo(this.getClass()).slash(product.getProductId()).withSelfRel();
        product.add(selfLink);
        return product;
    }
}
