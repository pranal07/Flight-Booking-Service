package com.flightbooking.repository;

import com.flightbooking.model.entity.Flight;

import java.util.List;
import java.util.Optional;

/**
 * Persistence abstraction for {@link Flight} aggregate access.
 * Implementations are responsible for concurrency semantics around seat inventory.
 */
public interface FlightRepository {

    Optional<Flight> findByFlightNumber(String flightNumber);

    List<Flight> findAll();

    Flight save(Flight flight);

    /**
     * Point-in-time check: whether the flight exists and has at least {@code requestedSeats} available.
     * Callers that need atomic guarantee must use {@link #reserveSeats(String, int)}.
     */
    boolean canAccommodate(String flightNumber, int requestedSeats);

    /**
     * Atomically reserves seats if the flight exists and has enough availability.
     *
     * @return {@code true} if seats were reserved; {@code false} if the flight is unknown,
     * {@code requestedSeats} is not positive, or inventory is insufficient.
     */
    boolean reserveSeats(String flightNumber, int requestedSeats);
}
