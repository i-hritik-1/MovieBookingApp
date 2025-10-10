package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.*;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.model.*;
import org.hritik.MovieBookingApp.repository.MovieRepo;
import org.hritik.MovieBookingApp.repository.ScreenRepo;
import org.hritik.MovieBookingApp.repository.ShowRepo;
import org.hritik.MovieBookingApp.repository.ShowSeatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        return mapToDto(savedShow,availableSeats);

    }

    public ShowDto getShowById(Long id)
    {
        Show show = showRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Show Not found by id : "+id));
        List<ShowSeat> availableSeats = showSeatRepo.findByShowIdAndStatus(show.getId(),"AVAILABLE");

        return mapToDto(show,availableSeats);
    }

    public List<ShowDto> gteAllShows()
    {
        List<Show> shows = showRepo.findAll();

        return shows.stream()
                .map(show -> {
                    List<ShowSeat> availableSeat = showSeatRepo.findByShowIdAndStatus(show.getId(),"AVAILABLE");
                    return mapToDto(show,availableSeat);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowByMovie(Long movieId)
    {
        List<Show> shows = showRepo.findByMovieId(movieId);

        return shows.stream()
                .map(show -> {
                    List<ShowSeat> seats = showSeatRepo.findByShowIdAndStatus(show.getId(),"AVAILABLE");
                            return mapToDto(show,seats);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowByMovieAndCity(Long movieId,String city)
    {
        List<Show> shows = showRepo.findByMovie_idAndScreen_Theater_City(movieId,city);

        return shows.stream()
                .map(show -> {
                    List<ShowSeat> seats = showSeatRepo.findByShowIdAndStatus(show.getId(),"AVAILABLE");
                    return mapToDto(show,seats);
                })
                .collect(Collectors.toList());
    }

    public List<ShowDto> getShowByDateRange(LocalDateTime startTime, LocalDateTime endTime)
    {
        List<Show> shows = showRepo.findByStartTimeBetween(startTime,endTime);

        return shows.stream()
                .map(show -> {
                    List<ShowSeat> seats = showSeatRepo.findByShowIdAndStatus(show.getId(),"AVAILABLE");
                    return mapToDto(show,seats);
                })
                .collect(Collectors.toList());
    }






    private ShowDto mapToDto(Show show, List<ShowSeat> availableSeats)
    {
        ShowDto showDto = new ShowDto();
        showDto.setId(show.getId());
        showDto.setStartTime(show.getStartTime());
        showDto.setEndTime(show.getEndTime());

        showDto.setMovie(new MovieDto(
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getMovie().getDescription(),
                show.getMovie().getLanguage(),
                show.getMovie().getGenre(),
                show.getMovie().getDurationMins(),
                show.getMovie().getReleaseDate(),
                show.getMovie().getPosterUrl()
        ));

        TheaterDto theaterDto = new TheaterDto(
                show.getScreen().getTheater().getId(),
                show.getScreen().getTheater().getName(),
                show.getScreen().getTheater().getAddress(),
                show.getScreen().getTheater().getCity(),
                show.getScreen().getTheater().getTotalScreen()
        );

        showDto.setScreen(new ScreenDto(
                show.getScreen().getId(),
                show.getScreen().getName(),
                show.getScreen().getTotalSeats(),
                theaterDto
        ));

        List<ShowSeatDto> seatDtos = availableSeats.stream()
                .map(seat ->{
                    ShowSeatDto seatDto = new ShowSeatDto();
                    seatDto.setId(seat.getId());
                    seatDto.setStatus(seat.getStatus());
                    seatDto.setPrice(seat.getPrice());

                    SeatDto baseseatdto = new SeatDto();
                    baseseatdto.setId(seat.getSeat().getId());
                    baseseatdto.setSeatNumber(seat.getSeat().getSeatNumber());
                    baseseatdto.setSeatType(seat.getSeat().getSeatType());
                    baseseatdto.setBasePrice(seat.getSeat().getBasePrice());

                    seatDto.setSeat(baseseatdto);
                    return seatDto;

                })
                .collect(Collectors.toList());


        showDto.setAvailableSeat(seatDtos);
        return showDto;
    }





}
