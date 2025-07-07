package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.dto.BookingDto;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookingApi {

    private final String BASE_URL = "https://restful-booker.herokuapp.com/";
    private final String BOOKING_ENDPOINT = "booking";
    private final String CONTENT_TYPE_JSON = "application/json";
    private final String AUTHORIZATION = "Basic YWRtaW46cGFzc3dvcmQxMjM=";

    public Response createBooking(BookingDto body) {
        return baseRequest()
                .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(body)
                .when()
                .post(BASE_URL + BOOKING_ENDPOINT)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getBookingIds() {
        return baseRequest()
                .when()
                .get(BASE_URL + BOOKING_ENDPOINT)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response updateBooking(Integer bookingId, BookingDto body) {
        return baseRequest()
                .headers(getAuthHeaders())
                .body(body)
                .when()
                .put(BASE_URL + BOOKING_ENDPOINT + "/" + bookingId)
                .then()
                .log().all()
                .extract()
                .response();
    }

    private RequestSpecification baseRequest() {
        return RestAssured.given()
                .log().all();
    }

    private Map<String, String> getAuthHeaders() {
        return Map.of(
                HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON,
                HttpHeaders.ACCEPT, CONTENT_TYPE_JSON,
                HttpHeaders.AUTHORIZATION, AUTHORIZATION
        );
    }
}