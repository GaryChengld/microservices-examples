package io.examples.grpc.greeting;

import io.grpc.ManagedChannel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;


public class Client extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(Client.class.getName());
    }

    @Override
    public void start() {
        ManagedChannel channel = VertxChannelBuilder
                .forAddress(vertx, "localhost", 8080)
                .usePlaintext(true)
                .build();
        GreeterGrpc.GreeterVertxStub stub = GreeterGrpc.newVertxStub(channel);
        HelloRequest request = HelloRequest.newBuilder().setName("Gary").build();
        stub.sayHello(request, asyncResponse -> {
            if (asyncResponse.succeeded()) {
                System.out.println("Succeeded " + asyncResponse.result().getMessage());
            } else {
                asyncResponse.cause().printStackTrace();
            }
        });
    }
}
