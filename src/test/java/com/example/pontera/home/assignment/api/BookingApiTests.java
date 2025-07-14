package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.api.testdata.BookingDataProvider;
import com.example.pontera.home.assignment.dto.BookingDto;
import com.example.pontera.home.assignment.util.JsonUtil;
import com.example.pontera.home.assignment.util.NullUtil;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

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

        Response creationResponse = getCreationResponse(newBooking);

        Integer createdBookingId = JsonUtil.extractBookingId(creationResponse);
        assumeThat(createdBookingId).isNotNull();

        Response getAllBookingsResponse = bookingApi.getBookingIds();
        getAllBookingsResponse.then().statusCode(HttpStatus.OK.value());

        Set<Integer> bookingIds = NullUtil.getNonNull(() -> JsonUtil.extractFieldAsIntSet(getAllBookingsResponse, "bookingid"))
                .orElse(new HashSet<>());

        assertThat(bookingIds)
                .contains(createdBookingId);

    }


    @Test
    void updateBooking_shouldReturnUpdatedDatesInResponse() {
        LocalDate initialCheckin = LocalDate.now().plusDays(7);
        LocalDate initialCheckout = LocalDate.now().plusDays(11);
        BookingDto newBooking = bookingDataProvider.createValidBooking(initialCheckin, initialCheckout);

        Response creationResponse = getCreationResponse(newBooking);

        Integer bookingId = JsonUtil.extractBookingId(creationResponse);
        assumeThat(bookingId).isNotNull();

        LocalDate updatedCheckout = LocalDate.now().plusDays(12);
        newBooking.getBookingdates().setCheckout(updatedCheckout);

        Response updateResponse = bookingApi.updateBooking(bookingId, newBooking);
        updateResponse.then().statusCode(HttpStatus.OK.value());

        String actualCheckout = updateResponse.jsonPath().getString("bookingdates.checkout");
        String expectedCheckout = updatedCheckout.toString();

        assertThat(actualCheckout)
                .isEqualTo(expectedCheckout);
    }

    private Response getCreationResponse(BookingDto newBooking) {
        Response creationResponse = bookingApi.createBooking(newBooking);
        assumeThat(creationResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        return creationResponse;
    }


}
