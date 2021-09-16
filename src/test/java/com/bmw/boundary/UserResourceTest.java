package com.bmw.boundary;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    void getUser() {
        Response response = given()
                .when()
                .get("/user/permit-all")
                .andReturn();

        response.then()
                .statusCode(200)
                .body(containsString("hello + anonymous, isHttps: false, authScheme: null, hasJWT: false"));
    }

    static String generateValidUserToken() {
        return Jwt.upn("zahan.link@gmail.com")
                .issuer("https://example.com/issuer")
                .groups("User")
                .claim(Claims.birthdate.name(), "1985-01-01")
                .sign();
    }

    @Test
    void getAllowedRole() {
        Response response = given().auth()
                .oauth2(generateValidUserToken())
                .when()
                .get("/user/roles-allowed").andReturn();

        response.then()
                .statusCode(200)
                .body(containsString(
                        "hello + zahan.link@gmail.com, isHttps: false, authScheme: Bearer, hasJWT: true, birthdate: 1985-01-01"));
    }

    static String generateValidAdminToken() {
        return Jwt.upn("zahan.link@gmail.com")
                .issuer("https://example.com/issuer")
                .groups("Admin")
                .claim(Claims.birthdate.name(), "1985-01-01")
                .sign();
    }

    @Test
    void getAdminRoll() {
        Response response = given().auth()
                .oauth2(generateValidAdminToken())
                .when()
                .get("/user/roles-allowed-admin").andReturn();

        response.then()
                .statusCode(200)
                .body(containsString(
                        "hello + zahan.link@gmail.com, isHttps: false, authScheme: Bearer, hasJWT: true, birthdate: 1985-01-01"));
    }

    @Test
    void getDeny() {
        Response response = given().auth()
                .oauth2(generateValidUserToken())
                .when()
                .get("/user/deny-all").andReturn();

        response.then().statusCode(403);
    }
}