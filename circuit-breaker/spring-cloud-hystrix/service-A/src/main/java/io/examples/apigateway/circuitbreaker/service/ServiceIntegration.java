package io.examples.apigateway.circuitbreaker.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.examples.apigateway.circuitbreaker.domain.ResultB;
import io.examples.apigateway.circuitbreaker.domain.ResultC;
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

    @HystrixCommand(fallbackMethod = "fallbackServiceB", commandKey = "default")
    public ResultB callServiceB() {
        String url = "http://ServiceB/serviceB";
        return restTemplate.getForObject(url, ResultB.class);
    }

    public ResultB fallbackServiceB(Throwable hystrixCommand) {
        ResultB resultB = new ResultB();
        resultB.setResult(hystrixCommand.getLocalizedMessage());
        return resultB;
    }

    @HystrixCommand(fallbackMethod = "fallbackServiceC", commandKey = "default")
    public ResultC callServiceC() {
        String url = "http://ServiceC/serviceC";
        return restTemplate.getForObject(url, ResultC.class);
    }

    public ResultC fallbackServiceC(Throwable hystrixCommand) {
        ResultC resultC = new ResultC();
        resultC.setResult(hystrixCommand.getLocalizedMessage());
        return resultC;
    }
}
