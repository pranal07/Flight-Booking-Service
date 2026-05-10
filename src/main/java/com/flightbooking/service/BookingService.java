package com.flightbooking.service;

import com.flightbooking.model.dto.BookingRequest;
import com.flightbooking.model.dto.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);
}
