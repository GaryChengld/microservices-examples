package io.examples.discovery.boot.eureka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyData {
    private String myDataId;
    private String dataContent;
    private ServiceData serviceData;
}
