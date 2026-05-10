package com.flightbooking.repository;

import com.flightbooking.model.entity.Flight;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryFlightRepositoryImpl implements FlightRepository {

    private final ConcurrentHashMap<String, Flight> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @PostConstruct
    void loadSampleFlights() {
        save(Flight.builder()
                .flightNumber("AA101")
                .totalSeats(150)
                .availableSeats(150)
                .build());
        save(Flight.builder()
                .flightNumber("UA202")
                .totalSeats(200)
                .availableSeats(180)
                .build());
        save(Flight.builder()
                .flightNumber("DL303")
                .totalSeats(120)
                .availableSeats(45)
                .build());
        save(Flight.builder()
                .flightNumber("WN404")
                .totalSeats(175)
                .availableSeats(175)
                .build());
    }

    @Override
    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return Optional.ofNullable(storage.get(flightNumber)).map(this::copyOf);
    }

    @Override
    public List<Flight> findAll() {
        return storage.values().stream()
                .map(this::copyOf)
                .sorted(Comparator.comparing(Flight::getFlightNumber))
                .toList();
    }

    @Override
    public Flight save(Flight flight) {
        Objects.requireNonNull(flight, "flight");
        String flightNumber = Objects.requireNonNull(flight.getFlightNumber(), "flightNumber");
        Flight persisted = storage.compute(flightNumber, (key, existing) -> {
            Long id = flight.getId() != null
                    ? flight.getId()
                    : (existing != null ? existing.getId() : idSequence.incrementAndGet());
            Flight entity = new Flight();
            entity.setId(id);
            entity.setFlightNumber(flightNumber);
            entity.setTotalSeats(flight.getTotalSeats());
            entity.setAvailableSeats(flight.getAvailableSeats());
            return entity;
        });
        return copyOf(persisted);
    }

    @Override
    public boolean canAccommodate(String flightNumber, int requestedSeats) {
        if (requestedSeats <= 0) {
            return false;
        }
        Flight current = storage.get(flightNumber);
        return current != null && current.getAvailableSeats() >= requestedSeats;
    }

    @Override
    public boolean reserveSeats(String flightNumber, int requestedSeats) {
        if (requestedSeats <= 0) {
            return false;
        }
        AtomicBoolean reserved = new AtomicBoolean(false);
        storage.computeIfPresent(flightNumber, (key, flight) -> {
            if (flight.getAvailableSeats() >= requestedSeats) {
                flight.setAvailableSeats(flight.getAvailableSeats() - requestedSeats);
                reserved.set(true);
            }
            return flight;
        });
        return reserved.get();
    }

    private Flight copyOf(Flight flight) {
        Flight copy = new Flight();
        copy.setId(flight.getId());
        copy.setFlightNumber(flight.getFlightNumber());
        copy.setTotalSeats(flight.getTotalSeats());
        copy.setAvailableSeats(flight.getAvailableSeats());
        return copy;
    }
}
