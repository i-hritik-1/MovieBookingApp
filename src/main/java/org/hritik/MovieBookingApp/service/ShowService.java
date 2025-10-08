package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.ShowDto;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.model.*;
import org.hritik.MovieBookingApp.repository.MovieRepo;
import org.hritik.MovieBookingApp.repository.ScreenRepo;
import org.hritik.MovieBookingApp.repository.ShowRepo;
import org.hritik.MovieBookingApp.repository.ShowSeatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepo showRepo;

    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private ScreenRepo screenRepo;

    @Autowired
    private ShowSeatRepo showSeatRepo;

    public ShowDto createShow(ShowDto showDto)
    {
        Show show = new Show();
        Movie movie = movieRepo.findById(showDto.getMovie().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Movie Not Found"));

        Screen screen = screenRepo.findById(showDto.getMovie().getId())
                .orElseThrow(()-> new ResourceNotFoundException("Screen Not Found"));

        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(showDto.getStartTime());
        show.setEndTime(showDto.getEndTime());
        Show savedShow = showRepo.save(show);

        List<ShowSeat> availableSeats = showSeatRepo.findByShowIdAndStatus(savedShow.getId(),"AVAILABLE");


    }

    private Show maptoEntity(ShowDto showDto)
    {
        Show show1=new Show();

        show1.setId(showDto.getId());
        show1.setStartTime(showDto.getStartTime());
        show1.setEndTime(showDto.getEndTime());

        Movie movie = new Movie();
        movie.setId(showDto.getMovie().getId());
        movie.setTitle(showDto.getMovie().getTitle());
        movie.setDescription(showDto.getMovie().getDescription());
        movie.setGenere(showDto.getMovie().getGenre());
        movie.setLanguage(showDto.getMovie().getLanguage());
        movie.setReleaseDate(showDto.getMovie().getReleaseDate());
        movie.setPosterUrl(showDto.getMovie().getPosterUrl());
        movie.setDurationMins(showDto.getMovie().getDurationMins());

        show1.setMovie(movie);

        List<Booking> bookings = showDto.get
        show1.setBookings();

    }





}
