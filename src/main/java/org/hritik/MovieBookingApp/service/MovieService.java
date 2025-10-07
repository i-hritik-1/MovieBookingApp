package org.hritik.MovieBookingApp.service;

import org.hritik.MovieBookingApp.dto.MovieDto;
import org.hritik.MovieBookingApp.exception.ResourceNotFoundException;
import org.hritik.MovieBookingApp.model.Movie;
import org.hritik.MovieBookingApp.repository.MovieRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepo movieRepo;

//    Create Movie
    public MovieDto creteMovie(MovieDto movieDto)
    {
        Movie saveMovie = mapToEntity(movieDto);
        movieRepo.save(saveMovie);

        MovieDto movieDto1 = mapToMovieDto(saveMovie);
        return movieDto;
    }

//    Update movie

    public MovieDto updateMovie(Long id, MovieDto movieDto)
    {
        Movie movie = movieRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found by id : " + id));

        Movie updatedMovie = mapToEntity(movieDto);
        movieRepo.save(updatedMovie);

        return mapToMovieDto(updatedMovie);
    }

//    Delete movie
    public void deleteMovie(Long id)
    {
        Movie movie = movieRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Movie not found by id : " + id));

        movieRepo.delete(movie);
    }



//    Find Movie by ID
    public MovieDto getMovieById(Long id)
    {
        Movie movie = movieRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Movie Not Found By Id :  "+ id));

        return mapToMovieDto(movie);
    }

//    Find all movies
    public List<MovieDto> getAllMovies()
    {
        List<Movie> allMovies = movieRepo.findAll();

        return allMovies.stream()
                .map(this::mapToMovieDto)
                .collect(Collectors.toList());
    }

//    Find Movie by Name
    public List<MovieDto> getMoviesByLanguage(String language)
    {
        List<Movie> movies = movieRepo.findByLanguage(language);

        return movies.stream()
                .map(this::mapToMovieDto)
                .collect(Collectors.toList());


    }

//    Find Movie by genre
    public List<MovieDto> getMoviesByGenre(String genre)
    {
        List<Movie> movies = movieRepo.findByGenre(genre);

        return movies.stream()
                .map(this::mapToMovieDto)
                .collect(Collectors.toList());
    }

//  Find movie by title
    public List<MovieDto> getMoviesByTitle(String title)
    {
        List<Movie> movies = movieRepo.findByTitleContaining(title);

        return movies.stream()
                .map(this::mapToMovieDto)
                .collect(Collectors.toList());
    }



//    DTO MAPPING
    private MovieDto mapToMovieDto(Movie movie)
    {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(movie.getId());
        movieDto.setTitle(movieDto.getTitle());
        movieDto.setLanguage(movie.getLanguage());
        movieDto.setDescription(movie.getDescription());
        movieDto.setGenre(movie.getGenere());
        movieDto.setDurationMins(movie.getDurationMins());
        movieDto.setPosterUrl(movie.getPosterUrl());
        movieDto.setReleaseDate(movie.getReleaseDate());

        return movieDto;
    }

    public Movie mapToEntity(MovieDto movieDto)
    {
        Movie movie = new Movie();
        movie.setId(movieDto.getId());
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setGenere(movieDto.getGenre());
        movie.setLanguage(movieDto.getLanguage());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setPosterUrl(movieDto.getPosterUrl());
        movie.setDurationMins(movieDto.getDurationMins());

        return movie;
    }

}
