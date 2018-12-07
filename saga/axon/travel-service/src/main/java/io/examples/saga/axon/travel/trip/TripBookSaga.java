package io.examples.saga.axon.travel.trip;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Gary Cheng
 */

@Saga
public class TripBookSaga {

    @Autowired
    private transient CommandGateway commandGateway;
}
