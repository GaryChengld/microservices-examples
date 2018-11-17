package io.examples.cqrs.petstore.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateEvent {
    private String id;
    private String name;
    private String category;
}
