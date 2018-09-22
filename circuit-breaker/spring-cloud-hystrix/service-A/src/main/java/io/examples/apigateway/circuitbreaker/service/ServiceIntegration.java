package io.examples.apigateway.circuitbreaker.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.examples.apigateway.circuitbreaker.domain.ResultB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The component to invoke other service
 *
 * @author Gary Cheng
 */
@Component
public class ServiceIntegration {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallbackServiceB")
    public ResultB callServiceB() {
        String url = "http://ServiceB/serviceB";
        return restTemplate.getForObject(url, ResultB.class);
    }

    public ResultB fallbackServiceB(Throwable hystrixCommand) {
        ResultB resultB = new ResultB();
        resultB.setResult(hystrixCommand.getLocalizedMessage());
        return new ResultB();
    }
}
