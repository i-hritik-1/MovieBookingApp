package org.hritik.MovieBookingApp.repository;

import org.hritik.MovieBookingApp.model.Booking;
import org.hritik.MovieBookingApp.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {

    List<Movie> findByLanguage(String language);
    List<Movie> findByGenre(String genre);
    List<Movie> findByTitleContaining(String title);

}
