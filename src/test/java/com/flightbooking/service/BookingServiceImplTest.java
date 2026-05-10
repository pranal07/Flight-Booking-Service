package com.flightbooking.service;

import com.flightbooking.constant.Constants;
import com.flightbooking.exception.FlightNotFoundException;
import com.flightbooking.exception.OverbookingException;
import com.flightbooking.model.dto.BookingRequest;
import com.flightbooking.model.dto.BookingResponse;
import com.flightbooking.model.entity.Flight;
import com.flightbooking.repository.FlightRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingServiceImpl")
class BookingServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("[+] createBooking succeeds and returns confirmed response")
    void createBooking_success() {
        stubFlightFound("AA101", 150, 150);
        when(flightRepository.canAccommodate("AA101", 1)).thenReturn(true);
        when(flightRepository.reserveSeats("AA101", 1)).thenReturn(true);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("AA101")
                .passengerName("Jane Doe")
                .seatsRequested(1)
                .build();

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response.getFlightNumber()).isEqualTo("AA101");
        assertThat(response.getSeatsBooked()).isEqualTo(1);
        assertThat(response.getPassengerName()).isEqualTo("Jane Doe");
        assertThat(response.getStatus()).isEqualTo(Constants.BOOKING_STATUS_CONFIRMED);
        assertThat(response.getBookingReference()).isNotBlank();
        verify(flightRepository).reserveSeats("AA101", 1);
    }

    @Test
    @DisplayName("[+] createBooking supports multiple seats")
    void createBooking_multipleSeats() {
        stubFlightFound("UA202", 200, 180);
        when(flightRepository.canAccommodate("UA202", 5)).thenReturn(true);
        when(flightRepository.reserveSeats("UA202", 5)).thenReturn(true);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("UA202")
                .passengerName("Alex Kim")
                .seatsRequested(5)
                .build();

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response.getSeatsBooked()).isEqualTo(5);
        verify(flightRepository).reserveSeats("UA202", 5);
    }

    @Test
    @DisplayName("[+] createBooking trims flight number and passenger name")
    void createBooking_trimsWhitespace() {
        stubFlightFound("WN404", 175, 175);
        when(flightRepository.canAccommodate(eq("WN404"), anyInt())).thenReturn(true);
        when(flightRepository.reserveSeats(eq("WN404"), anyInt())).thenReturn(true);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("  WN404  ")
                .passengerName("  Pat Lee  ")
                .seatsRequested(2)
                .build();

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response.getFlightNumber()).isEqualTo("WN404");
        assertThat(response.getPassengerName()).isEqualTo("Pat Lee");
    }

    @Test
    @DisplayName("[+] createBooking succeeds when inventory exactly matches request")
    void createBooking_exactInventoryMatch() {
        stubFlightFound("DL303", 120, 3);
        when(flightRepository.canAccommodate("DL303", 3)).thenReturn(true);
        when(flightRepository.reserveSeats("DL303", 3)).thenReturn(true);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("DL303")
                .passengerName("Chris Wu")
                .seatsRequested(3)
                .build();

        BookingResponse response = bookingService.createBooking(request);

        assertThat(response.getSeatsBooked()).isEqualTo(3);
        assertThat(response.getStatus()).isEqualTo(Constants.BOOKING_STATUS_CONFIRMED);
    }

    @Test
    @DisplayName("[+] createBooking propagates booking reference and message")
    void createBooking_responseContainsMessage() {
        stubFlightFound("AA101", 150, 10);
        when(flightRepository.canAccommodate("AA101", 2)).thenReturn(true);
        when(flightRepository.reserveSeats("AA101", 2)).thenReturn(true);

        BookingResponse response = bookingService.createBooking(BookingRequest.builder()
                .flightNumber("AA101")
                .passengerName("Sam")
                .seatsRequested(2)
                .build());

        assertThat(response.getMessage()).isEqualTo(Constants.BOOKING_CONFIRMED_MESSAGE);
    }

    @Test
    @DisplayName("[-] createBooking throws when flight is not found")
    void createBooking_flightNotFound() {
        when(flightRepository.findByFlightNumber("ZZ999")).thenReturn(Optional.empty());

        BookingRequest request = BookingRequest.builder()
                .flightNumber("ZZ999")
                .passengerName("Nobody")
                .seatsRequested(1)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage(Constants.ERROR_FLIGHT_NOT_FOUND);
    }

    @Test
    @DisplayName("[-] createBooking throws when seats cannot be accommodated")
    void createBooking_overbookingCanAccommodateFalse() {
        stubFlightFound("DL303", 120, 2);
        when(flightRepository.canAccommodate("DL303", 10)).thenReturn(false);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("DL303")
                .passengerName("Overbook")
                .seatsRequested(10)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(OverbookingException.class)
                .hasMessage(Constants.ERROR_INSUFFICIENT_SEATS);
    }

    @Test
    @DisplayName("[-] createBooking throws when atomic reserve fails")
    void createBooking_overbookingReserveReturnsFalse() {
        stubFlightFound("UA202", 200, 50);
        when(flightRepository.canAccommodate("UA202", 5)).thenReturn(true);
        when(flightRepository.reserveSeats("UA202", 5)).thenReturn(false);

        BookingRequest request = BookingRequest.builder()
                .flightNumber("UA202")
                .passengerName("Race")
                .seatsRequested(5)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(OverbookingException.class)
                .hasMessage(Constants.ERROR_INSUFFICIENT_SEATS);
    }

    @Test
    @DisplayName("[-] createBooking rejects non-positive seat count")
    void createBooking_invalidSeatCount() {
        BookingRequest request = BookingRequest.builder()
                .flightNumber("AA101")
                .passengerName("X")
                .seatsRequested(0)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Constants.ERROR_INVALID_SEAT_COUNT);
    }

    private void stubFlightFound(String flightNumber, int total, int available) {
        Flight flight = Flight.builder()
                .id(1L)
                .flightNumber(flightNumber)
                .totalSeats(total)
                .availableSeats(available)
                .build();
        when(flightRepository.findByFlightNumber(flightNumber)).thenReturn(Optional.of(flight));
    }
}
