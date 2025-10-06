package org.hritik.MovieBookingApp.repository;

import org.hritik.MovieBookingApp.model.Movie;
import org.hritik.MovieBookingApp.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepo extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long movieId);
    List<Show> findByScreenId(Long screenId);
    List<Show> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Show> findByMovie_idAndScreen_Theater_City(Long movieId, String city);

}
