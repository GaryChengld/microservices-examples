package io.examples.spring.hateoas.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

/**
 * @author Gary Cheng
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product extends ResourceSupport {
    @EqualsAndHashCode.Include
    private Integer productId;
    private String name;
    private String category;
}
