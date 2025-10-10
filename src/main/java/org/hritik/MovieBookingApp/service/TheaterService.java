package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.TheaterDto;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.model.Theater;
import org.hritik.MovieBookingApp.repository.TheaterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepo theaterRepo;

    public TheaterDto createTheater(TheaterDto theaterDto)
    {
        Theater theater = mapToEntity(theaterDto);

        Theater savedTheater = theaterRepo.save(theater);

        return mapToDto(theater);

    }

    //Self written
    public TheaterDto updateTheater(TheaterDto theaterDto, Long id)
    {
        TheaterDto theaterDto1 = getTheaterById(theaterDto.getId());
        Theater theater = mapToEntity(theaterDto1);

        Theater updatedTheater = theaterRepo.save(theater);

        return mapToDto(updatedTheater);
    }

    // Self written
    public void deleteTheater(Long id)
    {
        TheaterDto theaterDto = getTheaterById(id);
        Theater theater = mapToEntity(theaterDto);
        theaterRepo.delete(theater);

    }
    public TheaterDto getTheaterById(Long id)
    {
        Theater theater = theaterRepo.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Theater not found by id : " + id));

        return mapToDto(theater);
    }

    public List<TheaterDto> getAllTheater()
    {
        List<Theater> theaters = theaterRepo.findAll();

        return theaters.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

//        return theaters.stream()
//                .map(theater -> {
//                    Theater getTheater = theaterRepo.findById(theater.getId())
//                            .orElseThrow(()->new ResourceNotFoundException("Theater not found by id : " + theater.getId()));
//
//                    return mapToDto(getTheater);
//
//                })
//                .collect(Collectors.toList());
    }

    public List<TheaterDto> getTheaterByCity(String city)
    {
        List<Theater> theaters = theaterRepo.findByCity(city);

        return theaters.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    private TheaterDto mapToDto(Theater theater)
    {
        TheaterDto theaterDto = new TheaterDto();

        theaterDto.setId(theater.getId());
        theaterDto.setName(theater.getName());
        theaterDto.setAddress(theater.getAddress());
        theaterDto.setCity(theater.getCity());
        theaterDto.setTotalScreen(theater.getTotalScreen());

        return theaterDto;
    }

    private Theater mapToEntity(TheaterDto theaterDto)
    {
        Theater theater = new Theater();

        theater.setId(theaterDto.getId());
        theater.setName(theaterDto.getName());
        theater.setAddress(theaterDto.getAddress());
        theater.setCity(theaterDto.getCity());
        theater.setTotalScreen(theaterDto.getTotalScreen());

        return theater;
    }
}
