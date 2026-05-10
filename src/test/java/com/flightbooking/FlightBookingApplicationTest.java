package com.flightbooking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Application context (positive)")
class FlightBookingApplicationTest {

    @Test
    @DisplayName("Spring context loads with all beans")
    void contextLoads() {
    }
}
