package com.example.pontera.home.assignment.api;

import com.example.pontera.home.assignment.api.testdata.BookingDataProvider;
import com.example.pontera.home.assignment.dto.BookingDto;
import com.example.pontera.home.assignment.util.JsonUtil;
import com.example.pontera.home.assignment.util.RetriesUtil;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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


    @ParameterizedTest
    @ValueSource(ints = {2})
    void newBooking_shouldAppearInGetAllBookingsResults(Integer maxRetries) {
        RetriesUtil.runWithRetries(() -> {
            LocalDate checkinDate = LocalDate.now().plusDays(7);
            LocalDate checkoutDate = LocalDate.now().plusDays(9);
            BookingDto newBooking = bookingDataProvider.createValidBooking(checkinDate, checkoutDate);

            Response creationResponse = bookingApi.createBooking(newBooking);
            creationResponse.then().statusCode(HttpStatus.OK.value());

            Integer createdBookingId = JsonUtil.extractBookingId(creationResponse);

            Response getAllBookingsResponse = bookingApi.getBookingIds();
            getAllBookingsResponse.then().statusCode(HttpStatus.OK.value());

            Set<Integer> bookingIds = JsonUtil.extractFieldAsIntSet(getAllBookingsResponse, "bookingid");

            assertThat(bookingIds)
                    .contains(createdBookingId);
        }, maxRetries);

    }


    @ParameterizedTest
    @ValueSource(ints = {2})
    void updateBooking_shouldReturnUpdatedDatesInResponse(Integer maxRetries) {
        RetriesUtil.runWithRetries(() -> {
            LocalDate initialCheckin = LocalDate.now().plusDays(7);
            LocalDate initialCheckout = LocalDate.now().plusDays(11);
            BookingDto newBooking = bookingDataProvider.createValidBooking(initialCheckin, initialCheckout);

            Response creationResponse = bookingApi.createBooking(newBooking);
            creationResponse.then().statusCode(HttpStatus.OK.value());

            Integer bookingId = JsonUtil.extractBookingId(creationResponse);

            LocalDate updatedCheckout = LocalDate.now().plusDays(12);
            newBooking.getBookingdates().setCheckout(updatedCheckout);

            Response updateResponse = bookingApi.updateBooking(bookingId, newBooking);
            updateResponse.then().statusCode(HttpStatus.OK.value());

            String actualCheckout = updateResponse.jsonPath().getString("bookingdates.checkout");
            String expectedCheckout = updatedCheckout.toString();

            assertThat(actualCheckout)
                    .isEqualTo(expectedCheckout);
        }, maxRetries);
    }


}
