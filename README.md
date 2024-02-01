# MS-Customer Service

This service is designed to manage customer information and financial transactions for a banking or retail system. It
enables creating customers, updating balances through top-up, purchase, and refund transactions. The service is built
with Spring Boot and Docker for seamless deployment and scalability.

## Features

- **Customer Management**: Create and manage customer records, including personal details and account balance.
- **Transaction Handling**: Perform top-up, purchase, and refund operations on customer balances.
- **Docker Support**: Facilitates easy deployment and running in containerized environments.

## REST API

### Endpoints

|             Endpoint	              | Method |  Req. body  | Status |  Resp. body   | Description    	                |
|:----------------------------------:|:------:|:-----------:|:------:|:-------------:|:--------------------------------|
|            `/customers`            | `GET`  |     N/A     |  200   | CustomerDTO[] | Get all customers.              |
|            `/customers`            | `POST` | CustomerDTO |  201   |  CustomerDTO  | Create a new customer.          |
|     `/customers/{customerId}`      | `GET`  |     N/A     |  200   |  CustomerDTO  | Get customer by ID.             |
|  `/customers/{customerId}/topup`   | `POST` |   Amount    |  200   |  CustomerDTO  | Top up customer's balance.      |
| `/customers/{customerId}/purchase` | `POST` |   Amount    |  200   |  CustomerDTO  | Deduct from customer's balance. |
|  `/customers/{customerId}/refund`  | `POST` |   Amount    |  200   |  CustomerDTO  | Refund to customer's balance.   |

### Request & Response Models

- **CustomerDTO**: Data transfer object for customer information.
- **Amount**: Represents a monetary value for transactions.

## Development & Deployment

### Prerequisites

- Java 17
- Gradle
- Docker & Docker Compose

### Building and Running Locally

1. **Build the application:**

   ```bash
   ./gradlew build

2. **Run the application:**
   ```bash
   ./gradlew bootRun   
3. **Running with Docker:**

  ```bash
docker-compose up --build
