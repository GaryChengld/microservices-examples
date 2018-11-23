package io.examples.cqrs.petstore.queryobject;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Gary Cheng
 */
@Entity(name = "Product")
public class ProductQueryObject {

    public ProductQueryObject() {
    }

    public ProductQueryObject(String productId, String name, String category) {
        this.productId = productId;
        this.name = name;
        this.category = category;
    }

    @Id
    private String productId;
    private String name;
    private String category;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
