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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // CREATE BOOKING FOR USER
    @Transactional
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

        double totalAmount = selectedSeat.stream()
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

        return mapToBookingDto(saveBooking,selectedSeat);
    }

    @Transactional
    public BookingDto cancelBooking(Long id)
    {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Resource not found."));

        booking.setStatus("CANCELLED");

        List<ShowSeat> seats = showSeatRepo.findAll()
                .stream()
                .filter(seat -> seat.getBooking() != null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());

        seats.forEach(seat -> {
            seat.setStatus("AVAILABLE");
            seat.setBooking(null);
        });

        if(booking.getPayment()!=null)
        {
            booking.getPayment().setStatus("REFUNDED");
        }

        Booking updateBooking = bookingRepo.save(booking);
        showSeatRepo.saveAll(seats);

        return mapToBookingDto(updateBooking,seats);

    }


    // Find Booking by ID
    public BookingDto getBookingById(Long id)
    {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Booking Not Found."));

        List<ShowSeat> seats = showSeatRepo.findAll()
                .stream()
                .filter(seat -> seat.getBooking()!= null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());

        return mapToBookingDto(booking, seats);
    }

    // Find Booking by Booking Number
    public BookingDto getBookingByNumber(String bookingNumber)
    {
        Booking booking = bookingRepo.findByBookingNumber(bookingNumber)
                .orElseThrow(()->new ResourceNotFoundException("Booking Not Found."));

        List<ShowSeat> seats = showSeatRepo.findAll()
                .stream()
                .filter(seat -> seat.getBooking()!= null && seat.getBooking().getId().equals(booking.getId()))
                .collect(Collectors.toList());

        return mapToBookingDto(booking, seats);
    }

    // Find Booking by User

    public List<BookingDto> getBookingByUserId(Long id)
    {
        List<Booking> bookings = bookingRepo.findByUserId(id);

        return bookings.stream()
                .map(booking -> {
                    List<ShowSeat> seats = showSeatRepo.findAll()
                            .stream()
                            .filter( seat -> seat.getBooking() != null  && seat.getBooking().getId().equals(booking.getId()))
                            .collect(Collectors.toList());
                    return mapToBookingDto(booking,seats);
                })
                .collect(Collectors.toList());
    }

    // DTO MAPPING
    private BookingDto  mapToBookingDto(Booking booking, List<ShowSeat> seats)
    {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setBookingNumber(booking.getBookingNumber());
        bookingDto.setBookingTime(booking.getBookingTime());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setTotalAmount(booking.getTotalAmount());

        UserDto userDto = getUserDto(booking);
        bookingDto.setUser(userDto);

        ShowDto showDto = getShowDto(booking);
        bookingDto.setShow(showDto);


        List<ShowSeatDto> availableSeats = seats.stream()
                .map(seat -> {
                    ShowSeatDto showSeatDto = new ShowSeatDto();
                    showSeatDto.setId(seat.getId());
                    showSeatDto.setStatus(seat.getStatus());
                    showSeatDto.setPrice(seat.getPrice());

                    SeatDto seatDto = new SeatDto();
                    seatDto.setId(seat.getSeat().getId());
                    seatDto.setSeatNumber(seat.getSeat().getSeatNumber());
                    seatDto.setBasePrice(seat.getSeat().getBasePrice());
                    seatDto.setSeatType(seat.getSeat().getSeatType());

                    showSeatDto.setSeat(seatDto);

                    return showSeatDto;
                })
                .collect(Collectors.toList());

        bookingDto.setSeats(availableSeats);


        if(booking.getPayment() != null)
        {
            PaymentDto paymentDto = getPaymentDto(booking);
            bookingDto.setPayment(paymentDto);
        }



        return bookingDto;

    }

    private static UserDto getUserDto(Booking booking)
    {
        UserDto userDto = new UserDto();
        userDto.setId(booking.getUser().getId());
        userDto.setName(booking.getUser().getName());
        userDto.setEmail(booking.getUser().getEmail());
        userDto.setPhoneNUmber(booking.getUser().getPhone_number());

        return userDto;
    }

    private static ShowDto getShowDto(Booking booking)
    {


        ShowDto showDto = new ShowDto();
        showDto.setId(booking.getShow().getId());
        showDto.setStartTime(booking.getShow().getStartTime());
        showDto.setEndTime(booking.getShow().getEndTime());
        MovieDto movieDto = getMovieDto(booking);
        showDto.setMovie(movieDto);
        ScreenDto screenDto = getScreenDto(booking);
        showDto.setScreen(screenDto);

        return showDto;

    }

    private static MovieDto getMovieDto(Booking booking) {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(booking.getShow().getMovie().getId());
        movieDto.setDescription(booking.getShow().getMovie().getDescription());
        movieDto.setTitle(booking.getShow().getMovie().getTitle());
        movieDto.setLanguage(booking.getShow().getMovie().getLanguage());
        movieDto.setGenre(booking.getShow().getMovie().getGenre());
        movieDto.setDurationMins((booking.getShow().getMovie().getDurationMins()));
        movieDto.setReleaseDate(booking.getShow().getMovie().getReleaseDate());
        movieDto.setPosterUrl(booking.getShow().getMovie().getPosterUrl());
        return movieDto;
    }

    private static ScreenDto getScreenDto(Booking booking)
    {


        ScreenDto screenDto = new ScreenDto();
        screenDto.setId(booking.getShow().getScreen().getId());
        screenDto.setName(booking.getShow().getScreen().getName());
        screenDto.setTotalSeats(booking.getShow().getScreen().getTotalSeats());
        TheaterDto theaterDto = getTheaterDto(booking);
        screenDto.setTheater(theaterDto);

        return screenDto;
    }

    private static TheaterDto getTheaterDto(Booking booking)
    {
        TheaterDto theaterDto = new TheaterDto();
        theaterDto.setId(booking.getShow().getScreen().getTheater().getId());
        theaterDto.setName(booking.getShow().getScreen().getTheater().getName());
        theaterDto.setAddress(booking.getShow().getScreen().getTheater().getAddress());
        theaterDto.setTotalScreen(booking.getShow().getScreen().getTheater().getTotalScreen());

        return theaterDto;
    }

    private static PaymentDto getPaymentDto(Booking booking)
    {
        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setId(booking.getPayment().getId());
        paymentDto.setAmount(booking.getPayment().getAmount());
        paymentDto.setPaymentMethod(booking.getPayment().getPaymentMethod());
        paymentDto.setPaymentTime(booking.getPayment().getPaymentTime());
        paymentDto.setTransactionId(booking.getPayment().getTransactionId());
        paymentDto.setStatus(booking.getPayment().getStatus());

        return paymentDto;
    }


}