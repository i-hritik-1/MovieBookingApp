package org.hritik.MovieBookingApp.controller;


import jakarta.validation.Valid;
import org.hritik.MovieBookingApp.dto.BookingDto;
import org.hritik.MovieBookingApp.dto.BookingRequestDto;
import org.hritik.MovieBookingApp.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")

public class BookingController {

    @Autowired
    private BookingService bookingService;
    @GetMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingRequestDto bookingRequest)
    {
        return new ResponseEntity<>(bookingService.createBooking(bookingRequest))
    }

}
