package io.examples.apigateway.circuitbreaker.service;

import io.examples.apigateway.circuitbreaker.domain.ResultC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gary Cheng
 */
@RestController
@RequestMapping("/serviceC")
@Slf4j
public class ServiceCController {

    @GetMapping
    public ResultC service() {
        log.debug("Received service C request");
        ResultC result = new ResultC();
        result.setResult("Welcome to Service C");
        return result;
    }
}
