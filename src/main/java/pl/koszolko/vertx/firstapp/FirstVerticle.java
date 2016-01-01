package pl.koszolko.vertx.firstapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

public class FirstVerticle extends AbstractVerticle {

    private static final UserRepository USER_REPOSITORY = new UserRepository();

    @Override
    public void start(Future<Void> future) {
        Router router = buildRouter();

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    private Router buildRouter() {
        Router router = Router.router(vertx);

        router.route("/")
                .handler(routingContext -> routingContext.response()
                        .putHeader("content-type", "text/html")
                        .end("First Vert.x App"));

        router.get("/api/users/")
                .handler(routingContext -> routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(USER_REPOSITORY.getAll())));

        return router;
    }
}
