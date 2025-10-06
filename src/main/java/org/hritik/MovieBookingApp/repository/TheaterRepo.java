package org.hritik.MovieBookingApp.repository;

import org.hritik.MovieBookingApp.model.Movie;
import org.hritik.MovieBookingApp.model.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepo extends JpaRepository<Theater, Long> {

    List<Theater> findByShowId(String city);

}
