package io.examples.cqrs.petstore.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Gary Cheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
    private String id;
    private String name;
    private String category;
}
