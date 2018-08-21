package io.examples.rest.vertx.resteasy;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Gary Cheng
 */
@Data
@AllArgsConstructor
public class Greeting {
    private String message;
}
