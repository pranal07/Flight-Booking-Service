package com.flightbooking.service;

import com.flightbooking.constant.Constants;
import com.flightbooking.exception.FlightNotFoundException;
import com.flightbooking.exception.OverbookingException;
import com.flightbooking.model.dto.BookingRequest;
import com.flightbooking.model.dto.BookingResponse;
import com.flightbooking.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingServiceImpl implements BookingService {

    private final FlightRepository flightRepository;
    private final ConcurrentHashMap<String, Object> flightLocks = new ConcurrentHashMap<>();

    public BookingServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = Objects.requireNonNull(flightRepository);
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        validate(request);
        String flightNumber = request.getFlightNumber().trim();

        synchronized (lockFor(flightNumber)) {
            if (flightRepository.findByFlightNumber(flightNumber).isEmpty()) {
                throw new FlightNotFoundException(Constants.ERROR_FLIGHT_NOT_FOUND);
            }

            if (!flightRepository.canAccommodate(flightNumber, request.getSeatsRequested())) {
                throw new OverbookingException(Constants.ERROR_INSUFFICIENT_SEATS);
            }

            if (!flightRepository.reserveSeats(flightNumber, request.getSeatsRequested())) {
                throw new OverbookingException(Constants.ERROR_INSUFFICIENT_SEATS);
            }
        }

        return BookingResponse.builder()
                .bookingReference(UUID.randomUUID().toString())
                .flightNumber(flightNumber)
                .seatsBooked(request.getSeatsRequested())
                .passengerName(request.getPassengerName().trim())
                .status(Constants.BOOKING_STATUS_CONFIRMED)
                .message(Constants.BOOKING_CONFIRMED_MESSAGE)
                .build();
    }

    private Object lockFor(String flightNumber) {
        return flightLocks.computeIfAbsent(flightNumber, key -> new Object());
    }

    private void validate(BookingRequest request) {
        Objects.requireNonNull(request, "request");
        if (request.getFlightNumber() == null || request.getFlightNumber().isBlank()) {
            throw new IllegalArgumentException("Flight number is required.");
        }
        if (request.getPassengerName() == null || request.getPassengerName().isBlank()) {
            throw new IllegalArgumentException("Passenger name is required.");
        }
        if (request.getSeatsRequested() < 1) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_SEAT_COUNT);
        }
    }
}
