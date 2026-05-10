# User prompts (Flight Booking Service)

Chronological list of prompts used while building this project.


---

## 0. Main prompt

Take-home task: Flight Ticket Booking API (Spring Boot + Java)
 Design and implement a small REST API for a flight ticket booking system. Share the github project once done. 
  You’re not expected to spend more than 60 minutes.
Partial solutions are acceptable — focus on correctness and clarity. Detailed specs are missing on purpose. Focus on what you think are the most reasonable business requirements that can be met within 60 min of implementation. 
Implementation requirements
This exercise is expected to be performed using the help of AI agents. Use any AI system of your choice. 
Step 1 
Only AI (an agent of your choice) must be used, no manual coding allowed except fixing compilation issues if necessary. Use an AI agent or system of your choice. You must provide each and every AI prompt you used to create & update the solution. Commit every iteration in the git repository. The AI prompt used for the iteration must be in the commit message. 
Step 2
Improve the AI version of the solution using manual coding. Indicate in the commit message what are the areas of the generated code that you improved and why. What are the major issues with the generated solution that you might not have time to fix manually? Make a single commit with the final changes to the git repository. 
Technical expectations
Use Spring Boot and Java
Single application instance (no distributed systems concerns)
No authentication, authorization, rate limiting
No flight search or destination logic
All booking operations assume the client already knows the flight number, no search required
In-memory storage only (no database required)
Do not allow overbooking the flights
Do not need APIs to retrieve bookings. Only to book. 
Model REST endpoints as you see fit
Use appropriate HTTP methods and status codes
Deliverables
Project is published on Github and the link to the project is provided
Runnable Spring Boot project (Gradle or Maven)
Short README.md including:
how to run the service
example requests
what you would improve if you had more time

give me the best prompts to make it the best project i want proper and the best code structure and follow design principles proper interfaces , service classes controllers constants configs , beanConfig where all beans are created, factory methods , singletons , repository , proper design check the current design for a flight booking system.


---

---

## 1. Initial project structure

Create a Spring Boot 3.x project structure for a Flight Ticket Booking API using Java 21. Focus on a clean, layered architecture: controller, service, repository, and model packages. Define a Flight entity with flightNumber, totalSeats, and availableSeats. Define a BookingRequest DTO and a BookingResponse. Use Lombok for boilerplate. Include a Constants class for error messages and a AppConfig for any necessary Bean configurations. Only provide the folder structure and the basic model/DTO classes for now.

---

## 2. In-memory flight repository

Implement a thread-safe in-memory repository for Flights. Since we are not using a database, create a FlightRepository interface and an InMemoryFlightRepositoryImpl using a ConcurrentHashMap to store flight data. Pre-populate the map with 3-4 sample flights in a @PostConstruct method. Ensure the repository handles seat availability checks. Follow the Repository Pattern strictly.

---

## 3. Booking service and exception handling

Implement a BookingService interface and a BookingServiceImpl. The service should handle the core logic:

- Retrieve flight by number.
- Check for overbooking (ensure requestedSeats <= availableSeats).
- Update the flight's available seats in a thread-safe manner (use synchronized or AtomicInteger logic).
- Return a BookingResponse.
- Throw custom exceptions (e.g., FlightNotFoundException, OverbookingException) which should be handled by a @ControllerAdvice global exception handler.
- Ensure the service depends on the interface, not the implementation.

---

## 4. REST controller and README

Create a BookingController with a POST endpoint /api/v1/bookings. It should accept a BookingRequest and return a 201 Created status on success. Use proper HTTP status codes for errors (404 for missing flights, 400 for overbooking). Add a README.md with instructions on how to run the app using Maven/Gradle and provide example curl requests for booking a seat.

---

## 5. Run locally and JUnit tests

Fix the project so that it runs add all the necessary dependencies , annotations and  spring requirements so that i can run it locally ,also add all the junit test cases where 60 percent should be positive test cases and 40 percent should be negative test cases

---

## 6. Postman / cURL documentation

give me all postman curls for testing and add all those postman curls in a file in this project as well

---

## 7. Git ignore and prompt history

add git ignore files and also add a file in that add all the prompts ii have used till now.

---

*End of recorded prompts.*
