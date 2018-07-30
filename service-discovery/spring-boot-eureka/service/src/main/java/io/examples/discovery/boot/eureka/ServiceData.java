package io.examples.discovery.boot.eureka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 * @Date 7/12/2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceData {
    private String id;
    private String content;
}
