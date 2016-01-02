package pl.koszolko.vertx.firstapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;

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

        router.route("/api/users*")
                .handler(BodyHandler.create());

        router.post("/api/users")
                .handler(routingContext -> {
                    final User newUser = Json.decodeValue(routingContext.getBodyAsString(),
                            User.class);
                    USER_REPOSITORY.add(newUser);
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(newUser));
                });

        router.put("/api/users/:id")
                .handler(routingContext -> {
                    String id = routingContext.request().getParam("id");
                    JsonObject json = routingContext.getBodyAsJson();
                    if (id == null || json == null) {
                        routingContext.response().setStatusCode(400).end();
                    } else {
                        Optional<User> userOptional = USER_REPOSITORY.get(Long.valueOf(id));
                        if (userOptional.isPresent()) {
                            User user = userOptional.get();
                            user.setLogin(json.getString("login"));
                            routingContext.response()
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(user));
                        }
                    }
                });

        router.delete("/api/users/:id")
                .handler(routingContext -> {
                    String id = routingContext.request().getParam("id");
                    if (id == null) {
                        routingContext.response().setStatusCode(400).end();
                    } else {
                        USER_REPOSITORY.remove(Long.valueOf(id));
                        routingContext.response().setStatusCode(204).end();
                    }
                });

        router.get("/api/users/:id")
                .handler(routingContext -> {
                    String id = routingContext.request().getParam("id");
                    if (id == null) {
                        routingContext.response().setStatusCode(400).end();
                    } else {
                        Optional<User> user = USER_REPOSITORY.get(Long.valueOf(id));
                        if (user.isPresent()) {
                            routingContext.response()
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(user.get()));
                        } else {
                            routingContext.response().setStatusCode(400).end();
                        }
                    }
                });

        return router;
    }
}
