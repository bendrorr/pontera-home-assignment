package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.dto.BookingDto;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class BookingApi {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com/booking";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String AUTHORIZATION = "Basic YWRtaW46cGFzc3dvcmQxMjM=";

    public Response createBooking(BookingDto body) {
        return given()
                .log().all()
                .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(body)
                .when()
                .post(BASE_URL)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getBookingIds() {
        return given()
                .log().all()
                .when()
                .get(BASE_URL)
                .then()
                .log().all()
                .extract()
                .response();
    }


    public Response updateBooking(Integer bookingId, BookingDto body) {
        return given()
                .log().all()
                .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                .body(body)
                .when()
                .put(BASE_URL + "/" + bookingId)
                .then()
                .log().all()
                .extract()
                .response();
    }


}