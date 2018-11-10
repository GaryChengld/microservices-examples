package io.example.consul.service1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Gary Cheng
 */
@RequestMapping("/v1/service1")
@RestController
@Slf4j
public class Service1Controller {
    private String SERVICE2_URL = "http://service2/v1/service2";

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping
    public Mono<Service1Result> service2() {
        log.info("Received service1 request");
        return this.rxBuildData();
    }

    private Mono<Service1Result> rxBuildData() {
        Service1Result service1Result = new Service1Result();
        service1Result.setName("service1");
        service1Result.setMessage("Welcome to service1");
        log.info("Sending service request");
        return Mono.create(emitter ->
                webClientBuilder.build().get().uri(SERVICE2_URL).exchange()
                        .flatMap(res -> res.bodyToMono(Service2Result.class))
                        .doOnError(ex -> {
                            log.error(ex.getLocalizedMessage());
                            service1Result.setService2Result(new Service2Result("service2", ex.getLocalizedMessage()));
                            emitter.success(service1Result);
                        })
                        .subscribe(serviceData -> {
                            log.info("Received service data:{}", serviceData.toString());
                            service1Result.setService2Result(serviceData);
                            emitter.success(service1Result);
                        })
        );
    }
}
