package io.examples.apigateway.circuitbreaker.service;

import io.examples.apigateway.circuitbreaker.domain.ResultB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gary Cheng
 */
@RestController
@RequestMapping("/serviceB")
@Slf4j
public class ServiceBController {

    @GetMapping
    public ResultB service() {
        log.debug("Received service B request");
        ResultB result = new ResultB();
        result.setResult("Welcome to Service B");
        return result;
    }
}
