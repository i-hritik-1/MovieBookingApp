package org.hritik.MovieBookingApp.repository;

import org.hritik.MovieBookingApp.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByBookingNumber(String bookingNumber);
    List<Booking> findByShowId(Long id);
}
