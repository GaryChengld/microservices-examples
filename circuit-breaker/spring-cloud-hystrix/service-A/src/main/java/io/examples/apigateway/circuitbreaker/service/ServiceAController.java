package io.examples.apigateway.circuitbreaker.service;

import io.examples.apigateway.circuitbreaker.domain.Results;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gary Cheng
 */
@RestController
@RequestMapping("/serviceA")
@Slf4j
public class ServiceAController {
    @Autowired
    private ServiceIntegration serviceIntegration;

    @GetMapping
    public Results service() {
        log.debug("Received service A request");
        Results results = new Results();
        results.setResultA("Welcome to Service A");
        results.setResultB(serviceIntegration.callServiceB().getResult());
        results.setResultC(serviceIntegration.callServiceC().getResult());
        return results;
    }
}
