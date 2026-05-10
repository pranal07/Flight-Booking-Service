# API testing — cURL commands (Postman-compatible)

Use these with **Postman → Import → Raw text** (paste a single `curl` command), or run them in a terminal.

**Default base URL:** `http://localhost:8080`  
(Change the host/port if you use `--server.port=8081` or another host.)

---

## 1. Create booking — success (`201 Created`)

### 1a. AA101 — two seats

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"AA101","passengerName":"Jane Doe","seatsRequested":2}'
```

### 1b. UA202 — single seat

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"UA202","passengerName":"Alex Kim","seatsRequested":1}'
```

### 1c. WN404 — multiple seats

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"WN404","passengerName":"Pat Lee","seatsRequested":3}'
```

### 1d. DL303 — small booking (flight has limited inventory)

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"DL303","passengerName":"Chris Wu","seatsRequested":2}'
```

### 1e. Trimmed whitespace (same as valid flight `WN404`)

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"  WN404  ","passengerName":"  Sam Taylor  ","seatsRequested":1}'
```

---

## 2. Create booking — flight not found (`404 Not Found`)

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"ZZ999","passengerName":"John Smith","seatsRequested":1}'
```

---

## 3. Create booking — overbooking / insufficient seats (`400 Bad Request`)

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"DL303","passengerName":"Alex Lee","seatsRequested":999}'
```

---

## 4. Create booking — invalid seat count (`400 Bad Request`)

### 4a. Zero seats

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"AA101","passengerName":"Sam Taylor","seatsRequested":0}'
```

### 4b. Negative seats

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"AA101","passengerName":"Sam Taylor","seatsRequested":-1}'
```

---

## 5. Create booking — validation (`400 Bad Request`)

### 5a. Blank passenger name

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"AA101","passengerName":"   ","seatsRequested":1}'
```

### 5b. Blank flight number

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"flightNumber":"   ","passengerName":"Jane Doe","seatsRequested":1}'
```

### 5c. Empty JSON body (missing fields — may trigger validation or server error)

```bash
curl -i -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{}'
```

---

## 6. Silent body (terminal-friendly — HTTP code only)

Replace the URL/body as needed:

```bash
curl -s -o /dev/null -w "%{http_code}\n" -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{"flightNumber":"AA101","passengerName":"Quick Test","seatsRequested":1}'
```

---

## Pre-seeded flights

| Flight | Total seats | Initial available |
|--------|-------------|-------------------|
| AA101  | 150         | 150               |
| UA202  | 200         | 180               |
| DL303  | 120         | 45                |
| WN404  | 175         | 175               |

Inventory decreases after each successful booking.

---

## Postman import

- **cURL:** Postman → **Import** → **Raw text** → paste any command above.  
- **Collection:** Import `Flight-Booking-Service.postman_collection.json` from this folder.
