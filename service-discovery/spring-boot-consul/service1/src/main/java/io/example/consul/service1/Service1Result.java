package io.example.consul.service1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service1Result {
    private String name;
    private String message;
    @JsonProperty("service2 result")
    private Service2Result service2Result;
}
