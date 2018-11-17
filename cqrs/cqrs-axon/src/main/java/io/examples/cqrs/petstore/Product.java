package io.examples.cqrs.petstore;

import io.examples.cqrs.petstore.command.CreateProductCommand;
import io.examples.cqrs.petstore.event.ProductCreateEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * @author Gary Cheng
 */
@Aggregate
public class Product {
    @AggregateIdentifier
    private String productId;

    public Product() {
    }

    @CommandHandler
    public Product(CreateProductCommand command) {
        Assert.hasLength(command.getName(), "Product name can't be empty");
        apply(new ProductCreateEvent(command.getId(), command.getName(), command.getCategory()));
    }

    @EventSourcingHandler
    protected void on(ProductCreateEvent event) {
        this.productId = event.getId();
    }
}
