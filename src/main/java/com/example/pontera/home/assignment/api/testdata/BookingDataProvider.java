package com.example.pontera.home.assignment.api.testdata;

import com.example.pontera.home.assignment.dto.BookingDatesDto;
import com.example.pontera.home.assignment.dto.BookingDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingDataProvider {

    public BookingDto createValidBooking(LocalDate checkin, LocalDate checkout) {
        return BookingDto.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDatesDto.builder()
                        .checkin(checkin)
                        .checkout(checkout)
                        .build())
                .additionalneeds("Breakfast")
                .build();
    }
}
