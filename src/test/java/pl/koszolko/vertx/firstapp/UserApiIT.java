package pl.koszolko.vertx.firstapp;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Integration tests using rest-assured.
 * To run integration tests: mvn clean verify
 */
@RunWith(JUnitParamsRunner.class)
public class UserApiIT {

    @BeforeClass
    public static void setUp() throws InterruptedException {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.getInteger("http.port", 8080);
    }

    @AfterClass
    public static void tearDown() {
        RestAssured.reset();
    }

    @Test
    public void shouldReturnUserForGetWithCorrectId() {
        long id = 0;

        given().
                header("Accept-Encoding", "application/json").
                when().
                get("api/users/" + id).
                then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("login", equalTo("login69"));
    }

    @Test
    @Parameters({
            "2145, 404",
            "wrong_type_of_param, 400"
    })
    public void shouldReturnProperStatusCodeForGetWithWrongParam(String param, int statusCode) {
        given().
                header("Accept-Encoding", "application/json").
                when().
                get("api/users/" + param).
                then().
                assertThat().
                statusCode(statusCode);
    }

    @Test
    public void shouldReturnAllUsersForGet() {
        given().
                header("Accept-Encoding", "application/json").
                when().
                get("api/users/").
                then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body(getLoginById(0L), equalTo("login69")).
                body(getLoginById(1L), equalTo("extra_user")).
                body(getLoginById(2L), equalTo("some_sample_login"));
    }

    @Test
    public void shouldAddUserAndReturnObjectForPostWithProperJson() {
        given().
                header("Accept-Encoding", "application/json").
                body("{\"id\": 4, \"login\": \"new_super_user\"}").
                when().
                post("/api/users/").
                then().
                assertThat().
                statusCode(201).
                contentType(ContentType.JSON).
                body("login", equalTo("new_super_user")).
                body("id", equalTo(4));
    }

    @Test
    public void shouldRemoveUserForDeleteWithProperId() {
        given().
                header("Accept-Encoding", "application/json").
                when().
                delete("/api/users/1").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    @Parameters({
            "50120, 404",
            "wrong_type_of_param, 400"
    })
    public void shouldReturnProperStatusCodeForDeleteWithWrongParam(String id, int statusCode) {
        given().
                header("Accept-Encoding", "application/json").
                when().
                delete("api/users/" + id).
                then().
                assertThat().
                statusCode(statusCode);
    }

    @Test
    public void shouldUpdateUserForPutWithProperJson() {
        long id = 0;
        String expectedLogin = "updated_login";

        given().
                header("Accept-Encoding", "application/json").
                body("{\"id\": " + id + ", \"login\": \"" + expectedLogin + "\"}").
                when().
                put("/api/users/" + id).
                then().
                assertThat().
                statusCode(200).
                contentType(ContentType.JSON).
                body("login", equalTo(expectedLogin));
    }

    private String getLoginById(long id) {
        return String.format("find { user -> user.id == %s }.login", id);
    }

}
