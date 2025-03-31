package org.example.hw4.task1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class JsonplaceholderRestAPITest {

    static {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    // test 1
    @Test
    public void testAlbumTitleExists() {
        given()
                .when()
                .get("/albums")
                .then()
                .statusCode(200)
                .body("title", hasItem("omnis laborum odio"));
    }

    // test 2
    @Test
    public void testCommentsCount() {
        given()
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(200));
    }

    // test 3
    @Test
    public void testUserWithSpecificNameZipExists() {
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("find { it.name == 'Ervin Howell' }.address.zipcode", equalTo("90566-7771"));
    }

    // test 4
    @Test
    public void testCommentsWithBizEmail() {
        given()
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .body("email.findAll { it.endsWith('.biz') }", not(empty()));
    }

    // test 5
    @Test
    public void testCreateNewPost() {
        String requestBody = """
            {
                "userId": 11,
                "id": 101,
                "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .body("userId", equalTo(11))
                .body("title", containsString("sunt aut facere repellat"))
                .body("body", containsString("quia et suscipit"));
    }
}
