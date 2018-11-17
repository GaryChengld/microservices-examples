package io.examples.cqrs.petstore.queryobject;

import io.examples.cqrs.petstore.event.ProductCreateEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Gary Cheng
 */
@Component
public class ProductQueryObjectUpdater {
    @Autowired
    private ProductQueryObjectRepository productQueryObjectRepository;

    @EventHandler
    public void on(ProductCreateEvent event) {
        productQueryObjectRepository.save(new ProductQueryObject(event.getId(), event.getName(), event.getCategory()));
    }
}
