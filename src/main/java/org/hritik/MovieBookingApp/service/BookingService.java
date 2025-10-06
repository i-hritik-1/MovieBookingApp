package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.*;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.exception.SeatUnavailableException;
import org.hritik.MovieBookingApp.model.*;
import org.hritik.MovieBookingApp.repository.BookingRepo;
import org.hritik.MovieBookingApp.repository.ShowRepo;
import org.hritik.MovieBookingApp.repository.ShowSeatRepo;
import org.hritik.MovieBookingApp.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ShowRepo showRepo;

    @Autowired
    private ShowSeatRepo showSeatRepo;

    @Autowired
    private BookingRepo bookingRepo;

    public BookingDto createBooking(BookingRequestDto bookingRequest)
    {
        User user = userRepo.findById(bookingRequest.getUserId())
                .orElseThrow(()-> new ResourceNotFoundException("User Not Found"));

        Show show = showRepo.findById(bookingRequest.getShowId())
                .orElseThrow(()-> new ResourceNotFoundException("Show Not Found"));

        List<ShowSeat> selectedSeat = showSeatRepo.findAllById(bookingRequest.getSeatIds());

        for (ShowSeat seat : selectedSeat)
        {
            if(!"AVAILABLE".equals(seat.getStatus()))
            {
                throw new SeatUnavailableException("Seat "+seat.getSeat().getSeatNumber() + " is not available.");
            }

            seat.setStatus("LOCKED");
        }

        showSeatRepo.saveAll(selectedSeat);

        Double totalAmount = selectedSeat.stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();

        // Payment

        Payment payment = new Payment();
        payment.setAmount(totalAmount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentMethod(bookingRequest.getPaymentMethod());
        payment.setStatus("SUCCESS");
        payment.setTransactionId(UUID.randomUUID().toString());

        // Booking

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("CONFIRMED");
        booking.setTotalAmount(totalAmount);
        booking.setBookingNumber(UUID.randomUUID().toString());
        booking.setPayment(payment);

        Booking saveBooking = bookingRepo.save(booking);

        selectedSeat.forEach(seat->
        {
            seat.setStatus("BOOKED");
            seat.setBooking(saveBooking);
        });

        showSeatRepo.saveAll(selectedSeat);

        return
    }


//    private Long id; done
//    private String bookingNumber; done
//    private LocalDateTime bookingTime; done
//    private UserDto user; done
//    private String status; done
//    private double totalAmount; done
//    private List<ShowSeatDto> seats;
//    private PaymentDto payment;
//    private ShowDto show;
    private BookingDto  mapToBookingDto(Booking booking, List<ShowSeat> seats)
    {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setBookingNumber(booking.getBookingNumber());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setTotalAmount(booking.getTotalAmount());

        UserDto userDto = new UserDto();
        userDto.setId(booking.getUser().getId());
        userDto.setName(booking.getUser().getName());
        userDto.setEmail(booking.getUser().getEmail());
        userDto.setPhoneNUmber(booking.getUser().getPhone_number());

        bookingDto.setUser(userDto);

//        private Long id;
//        private LocalDateTime startTime;
//        private LocalDateTime endTime;
//        private MovieDto movie;
//        private ScreenDto screen;
//        private List<ShowSeatDto> availableSeat;

        ShowDto showDto = new ShowDto();
        showDto.setId(booking.getShow().getId());
        showDto.setStartTime(booking.getShow().getStartTime());
        showDto.setEndTime(booking.getShow().getEndTime());




    }
}
