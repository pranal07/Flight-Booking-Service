package com.flightbooking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private String bookingReference;
    private String flightNumber;
    private int seatsBooked;
    private String passengerName;
    private String status;
    private String message;
}
