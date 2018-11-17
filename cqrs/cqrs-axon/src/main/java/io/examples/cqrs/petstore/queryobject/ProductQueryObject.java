package io.examples.cqrs.petstore.queryobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Gary Cheng
 */
@Entity(name = "Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQueryObject {
    @Id
    private String productId;
    private String name;
    private String category;
}
