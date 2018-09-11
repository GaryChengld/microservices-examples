package io.examples.rest.boot.jersey;

import io.examples.store.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Main Application
 *
 * @author Gary Cheng
 */
@Configuration
@SpringBootApplication
public class JerseyApp {
    public static void main(String[] args) {
        SpringApplication.run(JerseyApp.class, args);
    }

    @Bean
    ProductRepository productRepository() {
        return ProductRepository.instance();
    }
}
