package io.examples.inventory.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents the inventory
 *
 * @author Gary Cheng
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {
    @EqualsAndHashCode.Include
    private int productId;
    @EqualsAndHashCode.Include
    private String location;
    private int quantity;
}
