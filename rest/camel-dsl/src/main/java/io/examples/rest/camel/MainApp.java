package io.examples.rest.camel;

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
@SpringBootApplication
@Configuration
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    @Bean
    ProductRepository productRepository() {
        return ProductRepository.instance();
    }
}
