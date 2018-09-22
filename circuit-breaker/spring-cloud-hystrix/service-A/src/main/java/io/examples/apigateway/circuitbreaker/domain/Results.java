package io.examples.apigateway.circuitbreaker.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Gary Cheng
 */
@Data
public class Results {
    @JsonProperty("ServiceA result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultA;
    @JsonProperty("ServiceB result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultB;
    @JsonProperty("ServiceC result")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resultC;
}
