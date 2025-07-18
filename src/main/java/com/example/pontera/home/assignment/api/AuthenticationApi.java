package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.dto.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApi {
    private static final String BASE_URI = "https://advisor-test.pontera.com";
    private static final String LOGIN_PATH = "/business/rest/api/users/authenticate";
    private static final String REFERER_HEADER = "https://advisor-test.pontera.com/business/auth/signin";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String QUERY_PARAMS = "?CT=null";

    public Response sendLoginRequest(LoginRequest loginRequest) {
        return createRequest(loginRequest)
                .post(QUERY_PARAMS)
                .then()
                .log().all()
                .extract()
                .response();
    }

    private RequestSpecification createRequest(LoginRequest loginRequest) {
        return RestAssured
                .given()
                .log().all()
                .baseUri(BASE_URI)
                .basePath(LOGIN_PATH)
                .header(HttpHeaders.REFERER, REFERER_HEADER)
                .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(loginRequest);
    }
}
