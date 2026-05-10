package com.flightbooking.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String ERROR_FLIGHT_NOT_FOUND = "Flight not found for the given flight number.";
    public static final String ERROR_INSUFFICIENT_SEATS = "Not enough seats available on this flight.";
    public static final String ERROR_INVALID_SEAT_COUNT = "Requested seat count must be at least one.";
    public static final String ERROR_BOOKING_FAILED = "Booking could not be completed. Please try again.";

    public static final String BOOKING_STATUS_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_CONFIRMED_MESSAGE = "Booking confirmed successfully.";
}
