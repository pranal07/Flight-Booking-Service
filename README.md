# Flight Booking Service

Spring Boot REST API for flight seat bookings. The service uses an in-memory flight catalog and a layered architecture (controller → service → repository).

See [`USER_PROMPTS.md`](USER_PROMPTS.md) for the prompts used to build this repo. A standard [`.gitignore`](.gitignore) ignores build output, IDE files, and local secrets.

## Requirements

- **Java 17+** (JDK). With JDK 21 you can run `mvn -Pjava21 test` or `mvn -Pjava21 spring-boot:run`.
- **Apache Maven 3.9+** (or compatible)

This project is configured for **Maven**. There is no Gradle wrapper in the repository; to use Gradle you would need to add your own `build.gradle` (or generate one) and map dependencies from `pom.xml`.

## Run the application (Maven)

From the project root:

```bash
mvn spring-boot:run
```

To build a runnable JAR and run it:

```bash
mvn -q -DskipTests package
java -jar target/flight-booking-service-0.0.1-SNAPSHOT.jar
```

Run the automated tests (15 tests: **9 positive**, **6 negative**):

```bash
mvn test
```

By default the server listens on **port 8080**. Override if needed:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## API: create a booking

**Endpoint:** `POST /api/v1/bookings`  
**Content-Type:** `application/json`

**Body (JSON):**

| Field            | Type   | Description        |
|------------------|--------|--------------------|
| `flightNumber`   | string | e.g. `AA101`       |
| `passengerName`  | string | Passenger full name |
| `seatsRequested` | number | Must be ≥ 1        |

**Success:** `201 Created` — response body is a `BookingResponse` (includes `bookingReference`, `flightNumber`, `seatsBooked`, etc.). A `Location` header points at `/api/v1/bookings/{bookingReference}`.

**Errors:**

| Situation                         | HTTP status |
|-----------------------------------|------------|
| Unknown flight                    | `404`      |
| Not enough seats (overbooking)    | `400`      |
| Invalid request (e.g. 0 seats)    | `400`      |

Pre-seeded flight numbers you can try: `AA101`, `UA202`, `DL303`, `WN404`.

**More examples:** see [`postman/API-CURL-commands.md`](postman/API-CURL-commands.md) (all cURL commands) and import [`postman/Flight-Booking-Service.postman_collection.json`](postman/Flight-Booking-Service.postman_collection.json) into Postman.

## Example `curl` requests

**Successful booking**

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AA101",
    "passengerName": "Jane Doe",
    "seatsRequested": 2
  }'
```

**Unknown flight (expect `404`)**

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "ZZ999",
    "passengerName": "John Smith",
    "seatsRequested": 1
  }'
```

**Overbooking / insufficient seats (expect `400`)**

Flight `DL303` is seeded with `45` available seats. Requesting more than remains available returns `400` with an error message in JSON:

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "DL303",
    "passengerName": "Alex Lee",
    "seatsRequested": 999
  }'
```

**Invalid seat count (expect `400`)**

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AA101",
    "passengerName": "Sam Taylor",
    "seatsRequested": 0
  }'
```

## Gradle (optional)

If you add a Gradle build to this project, a typical run would be:

```bash
./gradlew bootRun
```

Use the same base URL and JSON bodies as in the Maven section above.
