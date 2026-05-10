package com.flightbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbooking.constant.Constants;
import com.flightbooking.model.dto.BookingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BookingController HTTP (integration)")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("[+] POST /api/v1/bookings returns 201 Created")
    void postBooking_returnsCreated() throws Exception {
        BookingRequest body = BookingRequest.builder()
                .flightNumber("AA101")
                .passengerName("Integration User")
                .seatsRequested(1)
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flightNumber").value("AA101"))
                .andExpect(jsonPath("$.seatsBooked").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.bookingReference").isNotEmpty());
    }

    @Test
    @DisplayName("[+] POST /api/v1/bookings sets Location header")
    void postBooking_setsLocationHeader() throws Exception {
        BookingRequest body = BookingRequest.builder()
                .flightNumber("UA202")
                .passengerName("Header Check")
                .seatsRequested(1)
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/bookings/")));
    }

    @Test
    @DisplayName("[+] POST booking returns passenger name in body")
    void postBooking_returnsPassengerInBody() throws Exception {
        BookingRequest body = BookingRequest.builder()
                .flightNumber("WN404")
                .passengerName("Body Check")
                .seatsRequested(2)
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerName").value("Body Check"))
                .andExpect(jsonPath("$.seatsBooked").value(2));
    }

    @Test
    @DisplayName("[-] POST booking returns 404 for unknown flight")
    void postBooking_unknownFlight_returnsNotFound() throws Exception {
        BookingRequest body = BookingRequest.builder()
                .flightNumber("ZZ999")
                .passengerName("Nope")
                .seatsRequested(1)
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("[-] POST booking returns 400 when overbooking")
    void postBooking_overbooking_returnsBadRequest() throws Exception {
        BookingRequest body = BookingRequest.builder()
                .flightNumber("DL303")
                .passengerName("Too Many")
                .seatsRequested(999)
                .build();

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(Constants.ERROR_INSUFFICIENT_SEATS));
    }
}
