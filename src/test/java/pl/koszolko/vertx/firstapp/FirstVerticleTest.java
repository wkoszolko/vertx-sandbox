package pl.koszolko.vertx.firstapp;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simple tests using vertx-unit
 */
@RunWith(VertxUnitRunner.class)
public class FirstVerticleTest {

    private static final int port = 8082;
    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(
                FirstVerticle.class.getName(),
                options,
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldReturnGreetingMessage(TestContext context) {
        //given
        final Async async = context.async();

        //when
        vertx.createHttpClient().getNow(port, "localhost", "/",
                response -> response.handler(body -> {
                    //then
                    context.assertTrue(body.toString().contains("First Vert.x App"));
                    async.complete();
                }));
    }

    @Test
    public void shouldReturn201CodeAndNewUserWhenSendPost(TestContext context) {
        //given
        Async async = context.async();
        final String json = Json.encodePrettily(new User("master99"));
        final String length = Integer.toString(json.length());

        //when
        vertx.createHttpClient().post(port, "localhost", "/api/users")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    //then
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        User user = Json.decodeValue(body.toString(), User.class);
                        context.assertEquals(user.getLogin(), "master99");
                        context.assertNotNull(user.getId());
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }

}