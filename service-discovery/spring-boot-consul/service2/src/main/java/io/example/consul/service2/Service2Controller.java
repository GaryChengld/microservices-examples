package io.example.consul.service2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Gary Cheng
 */
@RequestMapping("/v1/service2")
@RestController
public class Service2Controller {

    @GetMapping
    public Mono<Service2Result> service2() {
        return Mono.just(new Service2Result("service2", "Welcome to service2"));
    }
}
