package io.examples.apigateway.circuitbreaker.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Gary Cheng
 */
@Data
public class ResultB {
    @JsonProperty("ServiceB result")
    private String result;
}
