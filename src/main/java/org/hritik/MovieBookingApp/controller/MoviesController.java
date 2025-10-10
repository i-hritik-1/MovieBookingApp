package org.hritik.MovieBookingApp.controller;

import jakarta.validation.Valid;
import org.hritik.MovieBookingApp.dto.MovieDto;
import org.hritik.MovieBookingApp.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MoviesController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/create")
    public ResponseEntity<MovieDto> createMovie(@Valid @RequestBody MovieDto movieDto)
    {
        return new ResponseEntity<>(movieService.creteMovie(movieDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id)
    {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }


    @GetMapping("/allMovies")
    public ResponseEntity<List<MovieDto>> getAllMovie()
    {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

}
