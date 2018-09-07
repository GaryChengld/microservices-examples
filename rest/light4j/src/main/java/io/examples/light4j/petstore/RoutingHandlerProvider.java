package io.examples.light4j.petstore;

import com.networknt.handler.HandlerProvider;
import io.examples.petstore.repository.RxProductRepository;
import io.examples.petstore.repository.impl.RxProductRepositoryImpl;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;

public class RoutingHandlerProvider implements HandlerProvider {
    private static final String PET_BASE_PATH = "/v1/pet";

    @Override
    public HttpHandler getHandler() {
        RxProductRepository productRepository = new RxProductRepositoryImpl();
        PetHandler petHandler = PetHandler.create(productRepository);

        return Handlers.routing()
                .get(PET_BASE_PATH + "/", petHandler::all)
                .get(PET_BASE_PATH + "/{id}", petHandler::byId)
                .get(PET_BASE_PATH + "/findByCategory/{category}", petHandler::byCategory)
                .post(PET_BASE_PATH + "/", petHandler::add)
                .put(PET_BASE_PATH + "/{id}", petHandler::update)
                .delete(PET_BASE_PATH + "/{id}", petHandler::delete);
    }
}
