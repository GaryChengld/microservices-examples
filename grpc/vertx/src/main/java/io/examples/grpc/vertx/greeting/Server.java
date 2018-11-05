package io.examples.grpc.vertx.greeting;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;


public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(Server.class.getName());
    }

    @Override
    public void start() {
        VertxServer server = VertxServerBuilder.forAddress(vertx, "localhost", 9080)
                .addService(this.greeterService()).build();
        server.start(ar -> {
            if (ar.succeeded()) {
                logger.debug("gRPC service started");
            } else {
                logger.debug("Could not start server " + ar.cause().getMessage());
            }
        });
    }

    private GreeterGrpc.GreeterVertxImplBase greeterService() {
        return new GreeterGrpc.GreeterVertxImplBase() {
            @Override
            public void sayHello(HelloRequest request, Future<HelloReply> future) {
                System.out.println("Hello " + request.getName());
                future.complete(HelloReply.newBuilder().setMessage("Reply message to " + request.getName()).build());
            }
        };
    }
}
