package io.examples.apigateway.circuitbreaker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


/**
 * Service A Application
 *
 * @author Gary Cheng
 */
@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
//@EnableFeignClien
public class ServiceAApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAApp.class, args);
    }
}
