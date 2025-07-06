package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.api.testdata.BookingDataProvider;
import com.example.pontera.home.assignment.dto.BookingDto;
import com.example.pontera.home.assignment.util.JsonUtil;
import com.example.pontera.home.assignment.util.NullUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {BookingApi.class, BookingDataProvider.class})
public class BookingApiTests {
    @Autowired
    private BookingApi bookingApi;
    @Autowired
    private BookingDataProvider bookingDataProvider;


    @Test
    void newBooking_shouldAppearInGetAllBookingsResults() {
        LocalDate checkinDate = LocalDate.now().plusDays(7);
        LocalDate checkoutDate = LocalDate.now().plusDays(9);
        BookingDto newBooking = bookingDataProvider.createValidBooking(checkinDate, checkoutDate);

        Response creationResponse = bookingApi.createBooking(newBooking);
        creationResponse.then().statusCode(HttpStatus.OK.value());

        Integer createdBookingId = extractBookingId(creationResponse);

        Response getAllBookingsResponse = bookingApi.getBookingIds();
        getAllBookingsResponse.then().statusCode(HttpStatus.OK.value());

        Set<Integer> bookingIds = JsonUtil.extractFieldAsIntSet(getAllBookingsResponse, "bookingid");

        assertThat(bookingIds)
                .contains(createdBookingId);
    }


    @Test
    void updateBooking_shouldReturnUpdatedDatesInResponse() {
        LocalDate initialCheckin = LocalDate.now().plusDays(7);
        LocalDate initialCheckout = LocalDate.now().plusDays(11);
        BookingDto newBooking = bookingDataProvider.createValidBooking(initialCheckin, initialCheckout);

        Response creationResponse = bookingApi.createBooking(newBooking);
        creationResponse.then().statusCode(HttpStatus.OK.value());

        Integer bookingId = extractBookingId(creationResponse);

        LocalDate updatedCheckout = LocalDate.now().plusDays(12);
        newBooking.getBookingdates().setCheckout(updatedCheckout);

        Response updateResponse = bookingApi.updateBooking(bookingId, newBooking);
        updateResponse.then().statusCode(HttpStatus.OK.value());

        String actualCheckout = updateResponse.jsonPath().getString("bookingdates.checkout");
        String expectedCheckout = updatedCheckout.toString();

        assertThat(actualCheckout)
                .isEqualTo(expectedCheckout);

    }

    private Integer extractBookingId(Response response) {
        JsonNode responseBody = response.jsonPath().getObject("$", JsonNode.class);
        return NullUtil.getOrNull(() -> responseBody.get("bookingid").asInt());
    }

}
