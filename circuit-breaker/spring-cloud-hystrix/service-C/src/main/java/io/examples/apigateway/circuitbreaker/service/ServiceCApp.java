package io.examples.apigateway.circuitbreaker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


/**
 * Service C Application
 *
 * @author Gary Cheng
 */
@SpringBootApplication
@EnableEurekaClient
public class ServiceCApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCApp.class, args);
    }
}
