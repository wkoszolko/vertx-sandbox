package pl.koszolko.vertx.firstapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class FirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        vertx
                .createHttpServer()
                .requestHandler(request -> request.response().end("First Vert.x App"))
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
}
