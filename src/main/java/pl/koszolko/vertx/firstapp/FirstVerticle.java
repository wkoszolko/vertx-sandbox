package pl.koszolko.vertx.firstapp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.commons.lang3.StringUtils;

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

        router.get("/api/users").handler(this::getAll);
        router.route("/api/users*").handler(BodyHandler.create());
        router.post("/api/users").handler(this::add);
        router.put("/api/users/:id").handler(this::update);
        router.delete("/api/users/:id").handler(this::delete);
        router.get("/api/users/:id").handler(this::getOne);

        return router;
    }

    private void getOne(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (!StringUtils.isNumeric(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Optional<User> user = USER_REPOSITORY.get(Long.valueOf(id));
            if (user.isPresent()) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(user.get()));
            } else {
                routingContext.response().setStatusCode(404).end();
            }
        }
    }

    private void delete(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (!StringUtils.isNumeric(id)) {
            routingContext.response().setStatusCode(400).end();
        } else {
            boolean wasDeleted = USER_REPOSITORY.remove(Long.valueOf(id));
            if (wasDeleted) {
                routingContext.response().setStatusCode(204).end();
            } else {
                routingContext.response().setStatusCode(404).end();
            }
        }
    }

    private void update(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (!StringUtils.isNumeric(id) || json == null) {
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
    }

    private void add(RoutingContext routingContext) {
        final User newUser = Json.decodeValue(routingContext.getBodyAsString(),
                User.class);
        USER_REPOSITORY.add(newUser);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(newUser));
    }

    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(USER_REPOSITORY.getAll()));
    }
}
