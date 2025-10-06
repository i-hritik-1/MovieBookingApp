package org.hritik.MovieBookingApp.repository;

import org.hritik.MovieBookingApp.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowSeatRepo extends JpaRepository<ShowSeat, Long> {

    List<ShowSeatRepo> findByShowId(Long movieId);
    List<ShowSeatRepo> findByShowIdAndStatus(Long showId, String status);
}
