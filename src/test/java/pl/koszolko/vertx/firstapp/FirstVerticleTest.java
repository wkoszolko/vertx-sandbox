package pl.koszolko.vertx.firstapp;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FirstVerticleTest {

    private static final int port = 8082;
    private Vertx vertx;

    @Test
    public void simpleTest(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/",
                response -> response.handler(body -> {
                    context.assertTrue(body.toString().contains("First Vert.x App"));
                    async.complete();
                }));
    }

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

}