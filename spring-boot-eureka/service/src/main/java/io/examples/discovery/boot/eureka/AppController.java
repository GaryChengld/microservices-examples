package io.examples.discovery.boot.eureka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @Author Gary Cheng
 */

@RestController
@Slf4j
public class AppController {

    @RequestMapping("/api/")
    @ResponseBody
    Mono<ServiceData> getData() {
        log.info("Received service request");
        Mono<ServiceData> data = buildData();
        log.info("return response");
        return this.buildData();
    }

    private Mono<ServiceData> buildData() {
        log.info("building data...");
        return Mono.just(new ServiceData(UUID.randomUUID().toString(), "Welcome to myService"));
    }
}
