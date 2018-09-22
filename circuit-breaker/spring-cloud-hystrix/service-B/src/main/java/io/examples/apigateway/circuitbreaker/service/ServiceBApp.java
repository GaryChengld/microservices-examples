package io.examples.apigateway.circuitbreaker.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


/**
 * Service B Application
 *
 * @author Gary Cheng
 */
@SpringBootApplication
@EnableEurekaClient
public class ServiceBApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceBApp.class, args);
    }
}
