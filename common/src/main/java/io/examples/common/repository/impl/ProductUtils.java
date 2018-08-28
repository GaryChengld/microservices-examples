package io.examples.common.repository.impl;

import io.examples.common.domain.Product;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gary Cheng
 */
public class ProductUtils {
    public static Collection<Product> initData() {
        Collection<Product> products = new HashSet<>();
        products.add(new Product(1, "Angelfish", "Fish"));
        products.add(new Product(2, "Tiger Shark", "Fish"));
        products.add(new Product(3, "Koi", "Fish"));
        products.add(new Product(4, "Goldfish", "Fish"));
        products.add(new Product(5, "Bulldog", "Dogs"));
        products.add(new Product(6, "Poodle", "Dogs"));
        products.add(new Product(7, "Dalmatian", "Dogs"));
        products.add(new Product(8, "Golden Retriever", "Dogs"));
        products.add(new Product(9, "Labrador Retriever", "Dogs"));
        products.add(new Product(10, "Chihuahua", "Dogs"));
        products.add(new Product(11, "Manx", "Cats"));
        products.add(new Product(12, "Persian", "Cats"));
        products.add(new Product(13, "Parrot", "Birds"));
        products.add(new Product(14, "Finch", "Birds"));
        return products;
    }
}