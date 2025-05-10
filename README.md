# Payment System API

A robust payment system API built with Spring Boot and Kotlin.

## Features

- User authentication and authorization
- Balance management
- Payment processing
- Transaction history
- Audit logging
- Rate limiting
- API documentation with Swagger

## Tech Stack

- Kotlin
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL
- JWT
- Docker
- Gradle

## Prerequisites

- JDK 17
- Docker and Docker Compose
- MySQL 8.0

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/payment-system.git
cd payment-system
```

2. Build and run with Docker Compose:
```bash
docker-compose up --build
```

3. Access the API:
- API Base URL: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html

## API Documentation

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe",
    "phoneNumber": "010-1234-5678"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

### Balance Management

#### Get Balance
```http
GET /api/balances
Authorization: Bearer <token>
```

#### Top Up Balance
```http
POST /api/balances/top-up
Authorization: Bearer <token>
Content-Type: application/json

{
    "amount": 10000.00
}
```

### Payment Processing

#### Process Payment
```http
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
    "amount": 5000.00,
    "description": "Payment for service"
}
```

#### Get Payment History
```http
GET /api/payments
Authorization: Bearer <token>
```

## Development

### Running Tests
```bash
./gradlew test
```

### Code Style
The project uses ktlint for code style checking:
```bash
./gradlew ktlintCheck
```

## Security

- JWT-based authentication
- Password encryption with BCrypt
- Rate limiting per IP and user
- Input validation
- Audit logging

## Monitoring

- Application logs in `./logs` directory
- Swagger UI for API monitoring
- Rate limit monitoring

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.


