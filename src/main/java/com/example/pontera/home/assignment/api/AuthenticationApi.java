package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.dto.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApi {

    private final String BASE_URI = "https://advisor-test.pontera.com";
    private final String LOGIN_PATH = "/business/rest/api/users/authenticate";
    private final String REFERER_HEADER = "https://advisor-test.pontera.com/business/auth/signin";
    private final String CONTENT_TYPE_JSON = "application/json";
    private final String QUERY_PARAMS = "?CT=null";

    public Response sendLoginRequest(LoginRequest loginRequest) {
        return createRequest(loginRequest)
                .post(QUERY_PARAMS);
    }

    private RequestSpecification createRequest(LoginRequest loginRequest) {
        return RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath(LOGIN_PATH)
                .header("Referer", REFERER_HEADER)
                .header("Content-Type", CONTENT_TYPE_JSON)
                .body(loginRequest);
    }
}
